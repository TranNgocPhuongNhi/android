package phuongnhi.tnpn.project;

public class Users {
    String id;
    String fullname;
    String email;
    String password;
    String authority;
    String image;
    String idUser;
    public Users() {

    }

    public Users(String id, String fullname, String email, String password, String authority, String image, String idUser) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.image = image;
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
