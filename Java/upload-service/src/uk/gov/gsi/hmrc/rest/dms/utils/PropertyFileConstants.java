package uk.gov.gsi.hmrc.rest.dms.utils;

import java.io.IOException;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.tools.RegistryPasswordUtils;

public class PropertyFileConstants extends PropFileReader {
	private String username;
	private String password;
	public PropertyFileConstants() throws IOException{
		super("RoboticAuthProps.properties");	
		setUsername(getValue(username));
		setPassword(getValue(password));

	}
	
	public String getUsername() {
		return username;
	}
	private void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	private void setPassword(String password) {
		try {
			this.password = RegistryPasswordUtils.decrypt(password);
		} catch (DfException dfe) {
			DfLogger.warn(this, "Error decrypting password", null, dfe);
		}
	}
}
