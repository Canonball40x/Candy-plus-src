package cc.candy.candymod.asm.api;

public class MappingName {
    private final String mappingName;
    private final String notchName;
    private final String originName;

    public MappingName(String mappingName, String notchName, String originName) {
        this.mappingName = mappingName;
        this.notchName = notchName;
        this.originName = originName;
    }

    public boolean equalName(String name) {
        return this.mappingName.equals(name) || notchName.equals(name) || this.originName.equals(name);
    }
}