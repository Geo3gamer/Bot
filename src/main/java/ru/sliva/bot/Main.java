package ru.sliva.bot;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.sliva.config.file.FileConfiguration;
import ru.sliva.config.file.YamlConfiguration;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main extends ListenerAdapter{
	
	private static FileConfiguration config;
	@SuppressWarnings("unused")
	private static JDA bot;
	private static Logger logger;
	private static String prefix;
	
	public static void main(String[] args) {
		prefix = "!";
		
		logger = LoggerFactory.getLogger("Bot");
		
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
			bot = builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		User user = e.getAuthor();
		if(user.isBot()) return;	
		Message msg = e.getMessage();
		if(e.isFromType(ChannelType.TEXT)) {
			Guild g = e.getGuild();
			MessageChannel ch = e.getChannel();
			String message = msg.getContentDisplay();
			if(message.startsWith(prefix)) {
				handleCommand(message.substring(1), ch, user);
			}
			logger.info("[" + g.getName() + "] [" + ch.getName() + "] " + user.getAsTag() + ": " + msg.getContentDisplay());
		} else {
			logger.info("[ЛС] " + user.getAsTag() + ": " + msg.getContentDisplay());
			PrivateChannel ch = e.getPrivateChannel();
			ch.sendMessage(new MessageBuilder().append(user).append(" вы не можете выполнять команды через лс.").build()).queue();
		}
	}
	
	private void handleCommand(String raw, MessageChannel ch, User user) {
		String[] rawList = raw.split(" ");
		String name = rawList[0];
		StringBuilder strBuild = new StringBuilder();
		for(int i = 1; i < rawList.length; i++) {
			strBuild.append(rawList[i] + " ");
		}
		@SuppressWarnings("unused")
		String[] args = strBuild.toString().trim().split(" ");
		if(name.equalsIgnoreCase("help")) {
			ch.sendMessage(new EmbedBuilder().setTitle("Команды", null).setColor(Color.CYAN).setDescription("TODO").setFooter("По запросу " + user.getAsTag()).setAuthor(null, null, user.getAvatarUrl()).build()).queue();
		}
	}
}
