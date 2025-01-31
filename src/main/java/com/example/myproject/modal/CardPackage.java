package com.example.myproject.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "card_packages")
public class CardPackage {
    @Id
    private String id;
    private String image;
    private String title1;
    private String title2;
    private String title3;
    private String title4;
    private String title5;
    
    

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



	public String getTitle1() {
		return title1;
	}



	public void setTitle1(String title1) {
		this.title1 = title1;
	}



	public String getTitle2() {
		return title2;
	}



	public void setTitle2(String title2) {
		this.title2 = title2;
	}



	public String getTitle3() {
		return title3;
	}



	public void setTitle3(String title3) {
		this.title3 = title3;
	}



	public String getTitle4() {
		return title4;
	}



	public void setTitle4(String title4) {
		this.title4 = title4;
	}



	public String getTitle5() {
		return title5;
	}



	public void setTitle5(String title5) {
		this.title5 = title5;
	}



	public CardPackage(String image, String title1, String title2, String title3, String title4, String title5) {
        this.image = image;
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.title4 = title4;
        this.title5 = title5;
    }
}
