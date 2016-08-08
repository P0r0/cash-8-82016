package co.tslc.cashe.android;

/**
 * 30/12/15.
 */
public class ListBanks {

    String id,name;
    public ListBanks(String id, String name){
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
