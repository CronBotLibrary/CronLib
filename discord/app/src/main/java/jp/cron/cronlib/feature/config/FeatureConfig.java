package jp.cron.cronlib.feature.config;

import com.google.gson.annotations.SerializedName;

public class FeatureConfig {

    @SerializedName("name")
    public String name;

    @SerializedName("id")
    public String id;

    @SerializedName("version")
    public String version;

    @SerializedName("mainClass")
    public String mainClass;

    @SerializedName("handleActivity")
    public boolean handleActivity;
}
