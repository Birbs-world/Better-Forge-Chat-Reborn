import com.rvt.bfcrmod.config.PlayerData;


public class PrintPermissions {
	public static void main(String[] args) {
		String enc = PlayerData.encodeStr("hello \\ world");
		System.out.println(enc);
		System.out.println(PlayerData.decodeStr(enc));
		System.out.println(PlayerData.decodeStr("\"\\u0049\""));
		System.out.println(PlayerData.decodeStr("\"..\\u004A\""));
		System.out.println(PlayerData.decodeStr("\"\\u004B..\""));
		System.out.println(PlayerData.decodeStr("\"..\\u004C..\""));
		
	}
}
