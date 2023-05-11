package net.javaguides.springboot.models;

public class Role {

  public enum ERole {

    ROLE_USER,
    ROLE_ADMIN

  }

    private Integer id;
    private ERole name;

    public Role() {

    }

    public Role(ERole name) {
      this.name = name;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public ERole getName() {
      return name;
    }

    public void setName(ERole name) {
      this.name = name;
    }
}