/** Quản lý chuyến bay bằng mảng đối tượng. */
public class QuanLyChuyenBay {
    private ChuyenBay[] danhSach;
    private int soLuong;
    public QuanLyChuyenBay(){
        danhSach=new ChuyenBay[1000];
        soLuong=0;
    }
    public boolean themChuyenBay(ChuyenBay chuyenBay){
        if(chuyenBay==null||soLuong>=danhSach.length){
            return false;
        }
        String maChuyenBay=chuyenBay.getMaChuyenBay();
        if(maChuyenBay==null||maChuyenBay.trim().isEmpty()) return false;
        if(timViTriChuyenBayTheoMa(maChuyenBay)!=-1) return false;
        danhSach[soLuong]=chuyenBay;
        soLuong++;
        return true;
    }
    private boolean xoaChuyenBayTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return false;
        for(int i=viTri;i<soLuong-1;i++){
            danhSach[i]=danhSach[i+1];
        }
        danhSach[soLuong-1]=null;
        soLuong--;
        return true;
    }
    public int timChuyenBay(ChuyenBay canTim){
        for(int i=0;i<soLuong;i++){
            if(danhSach[i]==canTim) return i;
        }
        return -1;
    }
    public int timViTriChuyenBayTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()){
            return -1;
        }

        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            String maChuyenBay=danhSach[i].getMaChuyenBay();
            if(maChuyenBay!=null&&maChuyenBay.equalsIgnoreCase(ma)){
                return i;
            }
        }
        return -1;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public void xuatDanhSach(){
        if(soLuong==0){
            System.out.println("Danh sach chuyen bay trong");
            return;
        }

        for(int i=0;i<soLuong;i++){
            System.out.println((i+1)+". "+danhSach[i]);
        }
    }
    public ChuyenBay[] timChuyenBayGanDungTheoMa(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new ChuyenBay[0];
        }

        String khoa=tuKhoa.trim().toUpperCase();
        ChuyenBay[] temp=new ChuyenBay[soLuong];
        int dem=0;

        for(int i=0;i<soLuong;i++){
            String maChuyenBay=danhSach[i].getMaChuyenBay();
            if(maChuyenBay!=null&&maChuyenBay.toUpperCase().contains(khoa)){
                temp[dem]=danhSach[i];
                dem++;
            }
        }

        ChuyenBay[] ketQua=new ChuyenBay[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
        }
        return ketQua;
    }
    public boolean xoaChuyenBayTheoMa(String maChuyenBay,QuanLyVe quanLyVe){
        if(quanLyVe==null) return false;
        int viTri=timViTriChuyenBayTheoMa(maChuyenBay);
        if(viTri==-1) return false;
        int soVe=quanLyVe.demVeTheoMaChuyenBay(maChuyenBay);
        if(soVe>0) return false;
        return xoaChuyenBayTaiViTri(viTri);
    }
    // Khong thay doi chuyen bay da co ve vi ve cu van tham chieu doi tuong cu.
    public boolean suaChuyenBayTheoMa(String maChuyenBay,ChuyenBay chuyenBayMoi,QuanLyVe quanLyVe){
        if(quanLyVe==null||chuyenBayMoi==null) return false;
        int viTri=timViTriChuyenBayTheoMa(maChuyenBay);
        if(viTri==-1) return false;
        String maMoi=chuyenBayMoi.getMaChuyenBay();
        if(maMoi==null||!maMoi.equalsIgnoreCase(danhSach[viTri].getMaChuyenBay())) return false;
        if(quanLyVe.demVeTheoMaChuyenBay(maChuyenBay)>0) return false;
        danhSach[viTri]=chuyenBayMoi;
        return true;
    }
    public ChuyenBay timChuyenBayTheoMa(String maCanTim){
        int viTri=timViTriChuyenBayTheoMa(maCanTim);
        if(viTri==-1) return null;
        return danhSach[viTri];
    }
    public ChuyenBay layChuyenBayTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong){
            return null;
        }
        return danhSach[viTri];
    }
}
