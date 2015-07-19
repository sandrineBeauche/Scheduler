class Person{
    private String name;
    private String firstname
    
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    
    public String getFirstname(){return this.firstname;}
    public void setFirstname(String firstname){this.firstname = firstname;}    
}

def person = new Person();
person.setName("Ben Mabrouk");
person.setFirstname("sandrine");
return person;