/** Quản lý hành khách bằng mảng đối tượng. */
public class QuanLyHanhKhach {
    private HanhKhach[] danhSach;
    private int soLuong;
    public QuanLyHanhKhach(){
        danhSach=new HanhKhach[1000];
        soLuong=0;
    }
    public boolean themHanhKhach(HanhKhach hanhKhach){
        if(hanhKhach==null||soLuong>=danhSach.length) return false;
        String maHanhKhach=hanhKhach.getMaHanhKhach();
        if(maHanhKhach==null||maHanhKhach.trim().isEmpty()) return false;
        if(timViTriHanhKhachTheoMa(maHanhKhach)!=-1) return false;
        danhSach[soLuong]=hanhKhach;
        soLuong++;
        return true;
    }
    public int getSoLuong(){
        return soLuong;
    }
    private boolean xoaHanhKhachTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return false;
        for(int i=viTri;i<soLuong-1;i++){
            danhSach[i]=danhSach[i+1];
        }
        danhSach[soLuong-1]=null;
        soLuong--;
        return true;
    }
    public int timViTriHanhKhach(HanhKhach canTim){
        for(int i=0;i<soLuong;i++){
            if(danhSach[i]==canTim) return i;
        }
        return -1;
    }
    public int timViTriHanhKhachTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()){
            return -1;
        }
        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            String maHanhKhach=danhSach[i].getMaHanhKhach();
            if(maHanhKhach!=null&&maHanhKhach.equalsIgnoreCase(ma)) return i;
        }
        return -1;
    }
    public void xuatDanhSach(){
        if(soLuong==0){
            System.out.println("Danh sach khach hang trong");
        }
        for(int i=0;i<soLuong;i++){
            System.out.println((i+1)+". "+danhSach[i]);
        }
    }
    public HanhKhach timHanhKhachTheoMa(String maCanTim){
        int viTri=timViTriHanhKhachTheoMa(maCanTim);
        if(viTri==-1){
            return null;
        }
        return danhSach[viTri];
    }
    public boolean xoaHanhKhachTheoMa(String maHanhKhach,QuanLyVe quanLyVe){
        if(quanLyVe==null) return false;
        int viTri=timViTriHanhKhachTheoMa(maHanhKhach);
        if(viTri==-1) return false;
        int soVe=quanLyVe.demVeTheoMaHanhKhach(maHanhKhach);
        if(soVe>0) return false;
        return xoaHanhKhachTaiViTri(viTri);
    }
    public HanhKhach[] timHanhKhachGanDungTheoTen(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new HanhKhach[0];
        }

        String khoa=tuKhoa.trim().toUpperCase();
        HanhKhach[] tam=new HanhKhach[soLuong];
        int dem=0;

        for(int i=0;i<soLuong;i++){
            String hoTen=danhSach[i].getHoTen();
            if(hoTen!=null&&hoTen.toUpperCase().contains(khoa)){
                tam[dem]=danhSach[i];
                dem++;
            }
        }

        HanhKhach[] ketQua=new HanhKhach[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=tam[i];
        }
        return ketQua;
    }
    public HanhKhach layHanhKhachTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return null;
        return danhSach[viTri];
    }
    public boolean suaTenHanhKhachTheoMa(String maHanhKhach,String hoTenMoi){
        if(hoTenMoi==null||hoTenMoi.trim().isEmpty()) return false;
        HanhKhach hanhKhach=timHanhKhachTheoMa(maHanhKhach);
        if(hanhKhach==null) return false;
        hanhKhach.setHoTen(hoTenMoi.trim());
        return true;
    }
}
