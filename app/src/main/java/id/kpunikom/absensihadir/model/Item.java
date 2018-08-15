package id.kpunikom.absensihadir.model;

public class Item {
    private String nama;
    private String email;
    private String jam_masuk;
    private String foto;

    public Item(String nama, String email, String jam_masuk, String foto) {
        this.nama = nama;
        this.email = email;
        this.jam_masuk = jam_masuk;
        this.foto = foto;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJam_masuk() {
        return jam_masuk;
    }

    public void setJam_masuk(String jam_masuk) {
        this.jam_masuk = jam_masuk;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}