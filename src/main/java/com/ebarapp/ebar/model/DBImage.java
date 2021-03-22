
package com.ebarapp.ebar.model;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "image")
public class DBImage extends BaseEntity {

	@Column(name = "file_name")
	private String	fileName;

	@Column(name = "file_type")
	private String	fileType;

	@Lob
	@Column(name = "data")
	private byte[]	data;


	public DBImage() {
	}

	public DBImage(final MultipartFile file) throws IOException {
		this.fileName = file.getOriginalFilename();
		this.fileType = file.getContentType();
		this.data = file.getBytes();
	}
}
