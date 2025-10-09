public class Cities {
    private String name;

    public Cities(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name=n;
    }

    @Override
    public String toString() {
        return name;
    }
}
