package co.tslc.cashe.android;

/**
 * 04/02/16.
 */
public class ListCompanies {
    String id,name;

    public ListCompanies(String id, String name){
        this.setId(id);
        this.setName(name);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

