package pro.butovanton.satellite;

import java.util.HashSet;
import java.util.List;

public class Sat {
    private String name;
    private HashSet<String> providers;

    public Sat() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getProviders() {
        return providers;
    }

    public void setProviders(HashSet<String> providers) {
        this.providers = providers;
    }

    public int getPosition() {
        return 0;
    }
}
