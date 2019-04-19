package aqth.yzw.iamlittle.EntityClass;

import org.litepal.crud.LitePalSupport;

public class BedAssign extends LitePalSupport {
    private long id;
    private long number;
    private String personName;
    private String assign;

    public long getId() {
        return id;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getPersonName() {
        return personName;
    }
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getAssign() {
        return assign;
    }

    public void setAssign(String assign) {
        this.assign = assign;
    }
}
