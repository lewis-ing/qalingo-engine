/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.core.domain.bean;

import java.io.Serializable;

public class GeolocData implements Serializable {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -4664799447578348774L;
    
    private String remoteAddress;
    private GeolocDataCountry country;
    private GeolocDataCity city;

    private String latitude;
    private String longitude;

    public GeolocData() {
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
    
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
    
    public GeolocDataCountry getCountry() {
        return country;
    }
    
    public void setCountry(GeolocDataCountry country) {
        this.country = country;
    }
    
    public GeolocDataCity getCity() {
        return city;
    }
    
    public void setCity(GeolocDataCity city) {
        this.city = city;
    }
    
    public String getLatitude() {
        return latitude;
    }
    
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    public String getLongitude() {
        return longitude;
    }
    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
}