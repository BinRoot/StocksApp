package API;

public class TrendsAPI {
	
	private static TrendsAPI trendsAPI;
	
	public static TrendsAPI getInstance() {
		if (trendsAPI == null) {
			trendsAPI = new TrendsAPI();
		}
		return trendsAPI;
	}
	
	
}
