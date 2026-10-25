/** Quản lý đại lý bằng mảng đối tượng. */
public class QuanLyDaiLy {
    private DaiLy[] danhSach;
    private int soLuong;
    public QuanLyDaiLy(){
        danhSach=new DaiLy[1000];
        soLuong=0;
    }
    public int timViTriDaiLyTheoMa(String maCanTim){
        if(maCanTim==null||maCanTim.trim().isEmpty()){
            return -1;
        }

        String ma=maCanTim.trim();
        for(int i=0;i<soLuong;i++){
            String maDaiLy=danhSach[i].getMaSo();
            if(maDaiLy!=null&&maDaiLy.equalsIgnoreCase(ma)){
                return i;
            }
        }
        return -1;
    }
    public boolean themDaiLy(DaiLy daiLy){
        if(daiLy==null||soLuong>=danhSach.length){
            return false;
        }

        String maDaiLy=daiLy.getMaSo();
        if(maDaiLy==null||maDaiLy.trim().isEmpty()){
            return false;
        }
        if(timViTriDaiLyTheoMa(maDaiLy)!=-1){
            return false;
        }

        danhSach[soLuong]=daiLy;
        soLuong++;
        return true;
    }
    public boolean xoaDaiLyTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong){
            return false;
        }

        for(int i=viTri;i<soLuong-1;i++){
            danhSach[i]=danhSach[i+1];
        }

        danhSach[soLuong-1]=null;
        soLuong--;
        return true;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public void xuatDanhSach(){
        if(soLuong==0){
            System.out.println("Danh sach dai ly trong");
            return;
        }
        for(int i=0;i<soLuong;i++){
            System.out.print((i+1)+". ");
            danhSach[i].hienThiThongTin();
        }
    }
    public DaiLy timDaiLyTheoMa(String maCanTim){
        int viTri=timViTriDaiLyTheoMa(maCanTim);
        if(viTri==-1){
            return null;
        }
        return danhSach[viTri];
    }
    public boolean xoaDaiLyTheoMa(String maDaiLy){
        int viTri=timViTriDaiLyTheoMa(maDaiLy);
        if(viTri==-1){
            return false;
        }
        return xoaDaiLyTaiViTri(viTri);
    }
    public DaiLy[] timDaiLyGanDungTheoMa(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new DaiLy[0];
        }

        String khoa=tuKhoa.trim().toUpperCase();
        DaiLy[] temp=new DaiLy[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            String maDaiLy=danhSach[i].getMaSo();
            if(maDaiLy!=null&&maDaiLy.toUpperCase().contains(khoa)){
                temp[dem]=danhSach[i];
                dem++;
            }
        }

        DaiLy[] ketQua=new DaiLy[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
        }
        return ketQua;
    }
    public DaiLy[] timDaiLyGanDungTheoTen(String tuKhoa){
        if(tuKhoa==null||tuKhoa.trim().isEmpty()){
            return new DaiLy[0];
        }

        String khoa=tuKhoa.trim().toUpperCase();
        DaiLy[] temp=new DaiLy[soLuong];
        int dem=0;
        for(int i=0;i<soLuong;i++){
            String tenDaiLy=danhSach[i].getHoTen();
            if(tenDaiLy!=null&&tenDaiLy.toUpperCase().contains(khoa)){
                temp[dem]=danhSach[i];
                dem++;
            }
        }

        DaiLy[] ketQua=new DaiLy[dem];
        for(int i=0;i<dem;i++){
            ketQua[i]=temp[i];
        }
        return ketQua;
    }
    public DaiLy layDaiLyTaiViTri(int viTri){
        if(viTri<0||viTri>=soLuong) return null;
        return danhSach[viTri];
    }
     public boolean suaTenDaiLyTheoMa(String maDaiLy,String hoTenMoi){
        if(hoTenMoi==null||hoTenMoi.trim().isEmpty()) return  false;
        DaiLy daiLy=timDaiLyTheoMa(maDaiLy);
        if(daiLy==null) return false;
        daiLy.setHoTen(hoTenMoi.trim());
        return true;
    }
}
