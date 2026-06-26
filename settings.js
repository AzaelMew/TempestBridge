import { @Vigilant, @TextProperty, @ColorProperty, @ButtonProperty, @SwitchProperty,@SelectorProperty,Color } from 'Vigilance';

@Vigilant("TempestBridge")
class Settings {
    @SelectorProperty({
        name: 'Message Author Color',
        description: '§2Guild > §b§nAzael_Nya§r §e[DISCORD]§f: Underlined part changes!',
        category: 'General',
        subcategory: 'General',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    messageAuthorColor = 11;
    @SelectorProperty({
        name: 'Discord Tag Color',
        description: '§2Guild > §bAzael_Nya§r §e§n[DISCORD]§f: Underlined part changes!',
        category: 'General',
        subcategory: 'General',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    discordTagColor = 14;
    @TextProperty({
        name: 'Ping Name',
        description: '§2Guild > §bAzael_Nya§r §e[DISCORD§e]§f: Hi &6&n@Azael&r!',
        category: 'General',
        subcategory: 'General',
        placeholder: 'Pingable Names',
    })
    pingName = "";
    @TextProperty({
        name: 'Discord Tag',
        description: '§2Guild > §bAzael_Nya§r §e[§nDISCORD§e]§f: Underlined part changes!',
        category: 'General',
        subcategory: 'General',
        placeholder: 'Choose the Tag',
    })
    discordTagText = 'DISCORD';
    @SelectorProperty({
        name: 'Guild Rank Tag Color',
        description: '§2Guild > §bAzael_Nya§r §6§n[Elder]§f: Message from sister guild!',
        category: 'General',
        subcategory: 'General',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    rankTagColor = 6;
    @SwitchProperty({
        name: 'Show Skyblock Events',
        description: `Do you want to see §bSkyblock Events §rin guild chat`,
        category: 'General',
        subcategory: 'General',
    })
    shouldShowEvent = true;
    @SwitchProperty({
        name: 'Should Skyblock Event Ping',
        description: `Do you want to hear a bling when §bSkyblock Events §ris happening`,
        category: 'General',
        subcategory: 'General',
    })
    shouldEventPing = false;
    @SelectorProperty({
        name: 'Skyblock Event Tag Color',
        description: `§2Guild > §6§n[EVENT] §bJacob's Farming Contest (Carrot, Melon, Sugar Cane): §fStarting in 2m!`,
        category: 'General',
        subcategory: 'General',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    eventTagColor = 6;
    @SelectorProperty({
        name: 'Skyblock Event Text Color',
        description: `§2Guild > §6[EVENT] §b§nJacob's Farming Contest (Carrot, Melon, Sugar Cane): §fStarting in 2m!`,
        category: 'General',
        subcategory: 'General',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    eventTextColor = 11;
    @SelectorProperty({
        name: 'Skyblock Event Time Color',
        description: `§2Guild > §6[EVENT] §bJacob's Farming Contest (Carrot, Melon, Sugar Cane): §f§nStarting in 2m!`,
        category: 'General',
        subcategory: 'General',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    eventTimeColor = 15;
    @SwitchProperty({
        name: "Show Discord Messages",
        description: "Toggle if you want to see the Discord messages or not",
        category: "General",
        subcategory: "General"
    })
    discordToggle = true;
    @SelectorProperty({
        name: 'Command Symbol',
        description: '§b§n-§b Skyblock Level§f:§e 400',
        category: 'General',
        subcategory: 'Commands',
        options: ["-", "*", "‣", "►", "➣", "➢", "❥", "✯","➤","➺"]
    })
    commandSymbol = 0;
    @SelectorProperty({
        name: 'Stat Text Color',
        description: '§b- §b§nSkyblock Level§f: §e§n400',
        category: 'General',
        subcategory: 'Commands',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    commandTextColor = 11;
    @SelectorProperty({
        name: 'Stat Value Color',
        description: '§b- Skyblock Level§f: §e§n400',
        category: 'General',
        subcategory: 'Commands',
        options: ["§0Black","§1Dark Blue","§2Dark Green","§3Dark Aqua","§4Dark Red","§5Dark Purple","§6Gold","§7Gray","§8Dark Gray","§9Blue","§aGreen","§bAqua","§cRed","§dPink","§eYellow","§fWhite"]
    })
    commandValueColor = 14;
    constructor() {
        this.initialize(this);
        this.addDependency("Should Skyblock Event Ping","Show Skyblock Events")

        this.addDependency("Skyblock Event Time Color","Show Skyblock Events")
        this.addDependency("Skyblock Event Text Color","Show Skyblock Events")
        this.addDependency("Skyblock Event Tag Color","Show Skyblock Events")
    }
}

export default new Settings;
