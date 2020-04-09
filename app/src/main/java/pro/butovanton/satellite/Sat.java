package pro.butovanton.satellite;

import java.util.HashSet;
import java.util.List;

public class Sat {
    private String name;
    private HashSet<String> providers;
    private int position;

    public Sat() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setPosition(name);
    }

    private void setPosition( String name) {
        String position = getPositionFromName( name );
        this.position = Integer.parseInt(position);
    }

    String getPositionFromName(String name) {
        String result = "";
        int i;
        final String st = "()EW";
        for (i = 0 ; i < name.length() - 1 ; i++ )
            if (name.charAt(i) == st.charAt(0)) {
             do {
                 i++;
                 result = result + name.charAt(i);
                }
                while ((name.charAt(i + 1) != st.charAt(1) ) && (name.charAt(i + 1) != st.charAt(2)) && (name.charAt(i + 1) != st.charAt(3)));
                break;
            }
        return result;
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
