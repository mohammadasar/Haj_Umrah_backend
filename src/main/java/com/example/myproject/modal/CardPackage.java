package com.example.myproject.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "card_packages")
public class CardPackage {
    @Id
    private String id;
    private String image;
    private String packageName;
    private String price;
    private String start;
    private String hotel;
    private String ticket;
    private String transport;
    private String meals;
    private String ziyarathTour;
    private String guide;
    private String kit;
    private String assist;
    private String visa;
    
    

    public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getImage() {
		return image;
	}



	public void setImage(String image) {
		this.image = image;
	}

    

	public String getPackageName() {
		return packageName;
	}



	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	public String getPrice() {
		return price;
	}



	public void setPrice(String price) {
		this.price = price;
	}



	public String getStart() {
		return start;
	}



	public void setStart(String start) {
		this.start = start;
	}



	public String getHotel() {
		return hotel;
	}



	public void setHotel(String hotel) {
		this.hotel = hotel;
	}



	public String getTicket() {
		return ticket;
	}



	public void setTicket(String ticket) {
		this.ticket = ticket;
	}



	public String getTransport() {
		return transport;
	}



	public void setTransport(String transport) {
		this.transport = transport;
	}



	public String getMeals() {
		return meals;
	}



	public void setMeals(String meals) {
		this.meals = meals;
	}



	public String getZiyarathTour() {
		return ziyarathTour;
	}



	public void setZiyarathTour(String ziyarathTour) {
		this.ziyarathTour = ziyarathTour;
	}



	public String getGuide() {
		return guide;
	}



	public void setGuide(String guide) {
		this.guide = guide;
	}



	public String getKit() {
		return kit;
	}



	public void setKit(String kit) {
		this.kit = kit;
	}



	public String getAssist() {
		return assist;
	}



	public void setAssist(String assist) {
		this.assist = assist;
	}



	public String getVisa() {
		return visa;
	}



	public void setVisa(String visa) {
		this.visa = visa;
	}



	public CardPackage(String image, String packageName, String price, String start, String hotel, String ticket, String transport, String meals, String ziyarathTour, String guide, String kit, String assist, String visa) {
        this.image = image;
        this.packageName = packageName;
        this.price = price;
        this.start = start;
        this.hotel = hotel;
        this.ticket = ticket;
        this.transport = transport;
        this.meals = meals;
        this.ziyarathTour = ziyarathTour;
        this.guide = guide;
        this.kit = kit;
        this.assist = assist;
        this.visa = visa;
    }
}
