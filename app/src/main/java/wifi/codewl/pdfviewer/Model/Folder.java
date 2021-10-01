package wifi.codewl.pdfviewer.Model;


public class Folder {
    private String name;
    private String path;
    private int number;

    public Folder(String name, String path,int number){
        this.name = name;
        this.path = path;
        this.number = number;

    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getNumber() {
        return number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
