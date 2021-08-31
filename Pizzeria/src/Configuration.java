public class Configuration implements IConfiguration{
    
    private static IConfiguration configuration = null;
    private Configuration(){
    }

    public static IConfiguration getConfiguration(){
      if(configuration==null){
          configuration = new Configuration();
      }
      return configuration;
    }

    public String getUser(){
        return System.getenv().get("user");
    }
    public String getPassword(){
        return System.getenv().get("password");
    }
    public String getUrl(){
        return System.getenv().get("url");
    }
}
