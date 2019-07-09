package com.papa.util.http;



public final class IpPortPair {

	
	public static final IpPortPair ANY_ONE = new IpPortPair("0.0.0.0",0);
	
	public static final IpPortPair RANDOM_ONE = new IpPortPair("0.1.2.3",0);


	public String ip;
	

	public int port;
	
	public IpPortPair(){}
	
	public IpPortPair(String ip, int port){
		this.ip = ip;
		this.port = port;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IpPortPair other = (IpPortPair) obj;
		if (ip == null) {
			if (other.ip != null) {
				return false;
			}
		} else if (!ip.equals(other.ip)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IpPortPair [ip=" + ip + ", port=" + port + "]";
	}
}
