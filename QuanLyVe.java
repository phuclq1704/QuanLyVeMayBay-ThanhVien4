/** Quản lý vé bằng mảng đối tượng VeMayBay[]. */
public class QuanLyVe {
    private VeMayBay[] danhSach;
    private boolean[] daHuy;
    private int soLuong;
    public QuanLyVe(){
        danhSach=new VeMayBay[1000];
        daHuy=new boolean[1000];
        soLuong=0;
    }
    public boolean themVe(VeMayBay ve){
        if(ve==null) return false;
        String maVe=ve.getMaVe();
        if(maVe==null||maVe.trim().isEmpty()) return false;
        if(timViTriVeTheoMa(maVe)!=-1) return false;
        if(soLuong>=danhSach.length){
            VeMayBay[] danhSachMoi=new VeMayBay[danhSach.length*2];
            boolean[] daHuyMoi=new boolean[daHuy.length*2];
            for(int i=0;i<soLuong;i++){
                danhSachMoi[i]=danhSach[i];
                daHuyMoi[i]=daHuy[i];
            }
            danhSach=danhSachMoi;
            daHuy=daHuyMoi;
        }
        danhSach[soLuong]=ve;
        daHuy[soLuong]=false;
        soLuong++;
        return true;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public boolean xoaVeTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return false;
        for(int i=viTri;i<soLuong-1;i++){
            danhSach[i]=danhSach[i+1];
            daHuy[i]=daHuy[i+1];
        }
        danhSach[soLuong-1]=null;
        daHuy[soLuong-1]=false;
        soLuong--;
        return true;
    }
    public int demVePhoThong(){
        int d=0;
        for(int i=0;i<soLuong;i++){
            if(danhSach[i] instanceof VePhoThong) d++;
        }
        return d;   
    }
    public int demVeThuongGia(){
        int d=0;
        for(int i=0;i<soLuong;i++){
            if(danhSach[i] instanceof VeThuongGia)
                d++;
        }
        return d;
    }
    public void xuatDanhSach(){
        if(soLuong==0){
            System.out.println("Danh sach ve trong");
        }
        for(int i=0;i<soLuong;i++){
            String trangThai=daHuy[i] ? "DA HUY" : "CON HIEU LUC";
            System.out.println((i+1)+". "+danhSach[i]+" ["+trangThai+"]");
        }
    }
    public int timViTriVeTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()) return -1;
        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            if(danhSach[i].getMaVe().equalsIgnoreCase(ma)) return i;
        }
        return -1;
    }
    public boolean xoaVeTheoMa(String maVe){
        int viTri=timViTriVeTheoMa(maVe);
        if(viTri==-1) return false;
        return xoaVeTaiViTri(viTri);
    }
    public boolean huyVeTheoMa(String maVe){
        int viTri=timViTriVeTheoMa(maVe);
        if(viTri==-1||daHuy[viTri]) return false;
        daHuy[viTri]=true;
        return true;
    }
    public boolean veConHieuLuc(String maVe){
        int viTri=timViTriVeTheoMa(maVe);
        return viTri!=-1&&!daHuy[viTri];
    }
    public boolean suaVeTheoMa(String maVe,VeMayBay veMoi){
        int viTri=timViTriVeTheoMa(maVe);
        if(viTri==-1||daHuy[viTri]||veMoi==null) return false;
        String maMoi=veMoi.getMaVe();
        if(maMoi==null||!maMoi.equalsIgnoreCase(danhSach[viTri].getMaVe())) return false;
        danhSach[viTri]=veMoi;
        return true;
    }
    public VeMayBay timVeTheoMa(String maVe){
        int viTri=timViTriVeTheoMa(maVe);
        if(viTri==-1){
            return null;
        }
        return danhSach[viTri];
    }
    public VeMayBay[] timVeGanDungTheoMa(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new VeMayBay[0];
        }
        String maKhoa=tuKhoa.trim().toUpperCase();
        VeMayBay[] temp=new VeMayBay[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            String maVe= danhSach[i].getMaVe();
            if(maVe!=null&&maVe.toUpperCase().contains(maKhoa)){
                temp[dem]=danhSach[i];
                dem++;
            }
        }
        VeMayBay[] ketQua=new VeMayBay[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
        }
        return ketQua;
    }
    public VeMayBay[] timVeGanDungTheoHanhKhach(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()) return new VeMayBay[0];
        String maKhoa= tuKhoa.trim().toUpperCase();
        VeMayBay[] temp=new VeMayBay[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            HanhKhach hanhKhach=danhSach[i].getHanhKhach();
            if(hanhKhach!=null){
                String hoTen=hanhKhach.getHoTen();
                if(hoTen!=null&&hoTen.toUpperCase().contains(maKhoa)){
                temp[dem]=danhSach[i];
                dem++;
               }
            }
           
        }
         VeMayBay[] ketQua=new VeMayBay[dem];
         for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
         }
         return ketQua;
    }
    public int demVeTheoMaChuyenBay(String maChuyenBay){
        if(maChuyenBay==null||maChuyenBay.trim().isEmpty()) return 0;
        String maCanDem=maChuyenBay.trim();
        int dem=0;
        for(int i=0;i<soLuong;i++){
            ChuyenBay chuyenBay=danhSach[i].getChuyenBay();
            if(chuyenBay!=null){
                String maCuaChuyen=chuyenBay.getSoHieuChuyenBay();
                if(maCuaChuyen!=null&&maCuaChuyen.equalsIgnoreCase(maCanDem)){
                    dem++;
                }
            }
        }
        return dem;
    }
    public int demVeConHieuLucTheoMaChuyenBay(String maChuyenBay){
        if(maChuyenBay==null||maChuyenBay.trim().isEmpty()) return 0;
        String maCanDem=maChuyenBay.trim();
        int dem=0;
        for(int i=0;i<soLuong;i++){
            if(daHuy[i]) continue;
            ChuyenBay chuyenBay=danhSach[i].getChuyenBay();
            if(chuyenBay!=null){
                String maCuaChuyen=chuyenBay.getSoHieuChuyenBay();
                if(maCuaChuyen!=null&&maCuaChuyen.equalsIgnoreCase(maCanDem)) dem++;
            }
        }
        return dem;
    }
    public VeMayBay layVeTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return null;
        return danhSach[viTri];
    }
    public int demVeTheoMaHanhKhach(String maHanhKhach){
        if(maHanhKhach==null||maHanhKhach.trim().isEmpty()) return 0;
        String maCanDem=maHanhKhach.trim();
        int dem=0;
        for(int i=0;i<soLuong;i++){
            HanhKhach hanhKhach=danhSach[i].getHanhKhach();
            if(hanhKhach!=null){
                String maCuaHanhKhach=hanhKhach.getMaSo();
                if(maCuaHanhKhach!=null&&maCuaHanhKhach.equalsIgnoreCase(maCanDem)) dem++;
            }
        }
        return dem;
    }
}
