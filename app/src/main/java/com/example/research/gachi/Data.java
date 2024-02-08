package com.example.research.gachi;

public class Data {
    int id;
    String number;
    String loc;
    String content;
    String comment;
    String url;
    double la;
    double lo;
    Boolean agree;
    String title;
    String date;
    public void setTitle(String title)
    {
        this.title=title;
    }
    public void setDate(String date)
    {
        this.date=date;
    }
    public void setId(int id)
    {
        this.id=id;
    }
    public void setLatitude(double la)
    {
        this.la=la;
    }
    public void setLongitude(double lo)
    {
        this.lo=lo;
    }
    public void setNumber(String number){this.number=number;}
    public void setLoc(String loc)
    {
        this.loc=loc;
    }

    public int getId()
    {
        return id;
    }
    public String getNumber()
    {
        return number;
    }
    public double getLatitude()
    {
        return la;
    }
    public double getLongitude()
    {
        return lo;
    }
    public String getTitle()
    {
        return title;
    }
    public String getDate()
    {
        return date;
    }
    public String getLoc()
    {
        return loc;
    }
    public String getContent()
    {
        return content;
    }
    public String getReason()
    {
        return url;
    }
    public String getComment() {return comment;}
    public boolean getAgree(){return agree;}
    public String getAgreeS(){
        if(agree==true) {
            return "찬성";
        }else
        {
            return "반대";
        }
    }


}
