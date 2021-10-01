package wifi.codewl.pdfviewer.Model;


public class PDF {
    private String name;
    private String path;

    public PDF(String name,String path){
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumber(){
        return 0;
    }
}
