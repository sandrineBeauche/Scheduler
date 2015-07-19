class Address{
    private String street;
    private String zipcode;
    private String country;
    
    public String getStreet(){return this.street;}
    public void setStreet(String street){this.street = street;}
    
    public String getZipcode(){return this.zipcode;}
    public void setZipcode(String zipcode){this.zipcode = zipcode;}
    
    public String getCountry(){return this.country;}
    public void setCountry(String country){this.country = country;}
}

class Person{
    private String name;
    private String firstname;
    private Address address;
    
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    
    public String getFirstname(){return this.firstname;}
    public void setFirstname(String firstname){this.firstname = firstname;}  
    
    public Address getAddress(){return this.address;}
    public void setAddress(Address address){this.address = address;}  
}

def person = new Person();
person.setName("Ben Mabrouk");
person.setFirstname("sandrine");

def address = new Address();
address.setStreet("24 cours de Bilbao");
address.setZipcode("35200");
address.setCountry("France");

person.setAddress(address);
return person;