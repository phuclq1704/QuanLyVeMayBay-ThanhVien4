import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap ma ve: ");
        String maVe = sc.nextLine();

        System.out.print("Nhap gia goc: ");
        double giaGoc = Double.parseDouble(sc.nextLine());

        System.out.println("1. Ve pho thong");
        System.out.println("2. Ve thuong gia");
        System.out.print("Chon loai ve: ");
        int loaiVe = Integer.parseInt(sc.nextLine());

        VeMayBay ve;

        if (loaiVe == 1) {
            System.out.print("Nhap phi hanh ly: ");
            double phi = Double.parseDouble(sc.nextLine());

            ve = new VePhoThong(maVe, giaGoc, phi);
        } else if (loaiVe == 2) {
            System.out.print("Nhap phi phong cho: ");
            double phi = Double.parseDouble(sc.nextLine());

            ve = new VeThuongGia(maVe, giaGoc, phi);
        } else {
            System.out.println("Loai ve khong hop le!");
            sc.close();
            return;
        }

        double tongTien = ve.tinhGiaVe();

        System.out.println("Ma ve: " + ve.getMaVe());
        System.out.println("Tong tien: " + tongTien);

        System.out.println("1. Tien mat");
        System.out.println("2. The");
        System.out.print("Chon cach thanh toan: ");
        int cachThanhToan = Integer.parseInt(sc.nextLine());

        IThanhToan tt;

        if (cachThanhToan == 1) {
            System.out.print("Nhap tien khach dua: ");
            double tien = Double.parseDouble(sc.nextLine());

            tt = new ThanhToanTienMat(tien);
            tt.thanhToan(tongTien);
        } else if (cachThanhToan == 2) {
            System.out.print("Nhap so the: ");
            String soThe = sc.nextLine();

            tt = new ThanhToanThe(soThe);
            tt.thanhToan(tongTien);
        } else {
            System.out.println("Cach thanh toan khong hop le!");
        }

        sc.close();
    }
}