package ru.sliva.bot;

import java.io.File;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.sliva.config.file.FileConfiguration;
import ru.sliva.config.file.YamlConfiguration;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main extends ListenerAdapter{
	
	public static String prefix;
	public static FileConfiguration config;
	
	public static void main(String[] args) {
		prefix = "!";
		
		Logger logger = LoggerFactory.getLogger("Bot");
		
		File cnfFile = new File("config.yml");
		if(!cnfFile.exists()) {
			try {
				cnfFile.createNewFile();
				logger.info("Creating a new configuration...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		config = YamlConfiguration.loadConfiguration(cnfFile);
		
		logger.info("Конфигурация инициализирована, запуск ядра...");
		String token = config.getString("token");
		String status = config.getString("status");
		
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setCompression(Compression.NONE);
		Activity act = Activity.of(ActivityType.DEFAULT, status);
		builder.setActivity(act);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.addEventListeners(new Main());
		
		try {
			builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
}
