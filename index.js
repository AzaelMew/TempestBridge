import Settings from "./settings";
import STuFLib from "../STuFLib/index"
let ignores = JSON.parse(FileLib.read("TempestBridge", "ignores.json")) ?? [];
let colors = ["§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"];
let symbols = ["-", "*", "‣", "►", "➣", "➢", "❥", "✯","➤","➺"]

//allows for quickly changing the code if account changes
const accounts = ["TempestBridge", "MrTheAFK", "lfForagingUpdate","[VIP] TempestBridge [Elder]","[VIP] lfForagingUpdate [Elder]","MrTheAFK [Elder]"]
// (chatType, botName, message)
const criteria = /^(Guild|Officer) > (?:\[[\w+]+\] )?(\w{1,16})(?: \[\w+\])?: (.+)$/
// NOTE: type can be: networth, stats, slayers, cata etc.
const typeRegex = /(?:\w{1,16})'s (\w+):/
const replaceList = {
  "Skill Avg": "Skill Average",
  "Farm": "Farming Level",
  "Mine": "Mining Level",
  "Comb": "Combat Level",
  "Forage": "Foraging Level",
  "Fish": "Fishing Level",
  "Ench": "Enchanting Level",
  "Alch": "Alchemy Level",
  "Carp": "Carpentry Level",
  "Rune": "Runecrafting Level",
  "Soci": "Social Level",
  "Taming": "Taming Level",
  "Slayer": "Slayer XP",
  "Cata": "☠ Cata Level",
  "Average": "Φ Class Average",
  "Archer": "☣ Archer Level",
  "Berserk": "⚔ Berserk Level",
  "Healer": "❤ Healer Level",
  "Mage": "✎ Mage Level",
  "Tank": "❈ Tank Level"
}

// This is only for the stats stuff not for the entire module itself
//functions
function resolveTextComponent(text) {
  let words = text.split(" ")
  let modifiedIndices = []

  for (let i = 0; i < words.length; i++) {
    let working = stripFormatting(words[i])
    if (working.startsWith("l$")) {
      let link = STuFLib.decode(working)
      words[i] = link
      modifiedIndices.push(i)
    }
  }

  if (modifiedIndices.length > 0) {
    let retVal = [true, new TextComponent(words.join(" "))]
    return retVal;
  } else {
    return [false]
  }
}

function stripFormatting(input) {
  let regex = /§[0-9A-FK-OR]/gi;
  return input.replace(regex, "")
}

function stripCurl(input) {
  const regex = /\{[^}]*\}$/;
  return input.replace(regex, '');
}

function escapeRegex(string) {
  return string.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');  // Escapes special characters
}

function ignorecheck(msgString, json, account) {
  // Escape special characters in account to make it safe for regex use
  const escapedAccount = escapeRegex(account);

  for (let i = 0; i < json.length; i++) {
    if (msgString.includes("Guild > " + account + ": " + json[i].ign + ":")) {
      return true;
    } else {
      // Construct regex to match "Guild > account: ign [Rec]:"
      const regex = new RegExp(
        `^Guild > ${escapedAccount}: ${json[i].ign} \\[[A-Za-z]{1,16}\\]:`,
        'gm'
      );
      
      if (regex.test(msgString)) {
        return true;
      }
    }
  }

  return false;
}

//message handling rules
function handleNetworth(msgString, event) {//!nw
  cancel(event)
  ChatLib.chat(`${colors[Settings.messageAuthorColor]}> ${msgString
    .replace(/˚/g, "\n")
    .replace(/(\w+): \$/g, colors[Settings.commandTextColor] + symbols[Settings.commandSymbol] + " $1&f: " + colors[Settings.commandValueColor] + "$")}`)
}

function handleStats(msgString, event) { //!stats
  cancel(event)

  const msg = msgString
    .replace(/˚/g, "\n")
    .replace(/([\w ]+): ([\d\-,$]+)/g, (_, m2, m3) => {
      if (!(m2.trim() in replaceList)) return ` ${colors[Settings.commandTextColor]+ symbols[Settings.commandSymbol]} ${m2.trim()}&f: ${colors[Settings.commandValueColor]}${m3}`
      return ` ${colors[Settings.commandTextColor]+ symbols[Settings.commandSymbol]} ${replaceList[m2.trim()]}&f: ${colors[Settings.commandValueColor]}${m3}`
    })
  ChatLib.chat(`${colors[Settings.commandTextColor]}> ${msg.replace("☠ ","")
  }`)
}

function handleCata(msgString, event) { //!cata
  cancel(event)

  const msg = msgString
    .replace(/˚/g, "\n")
    .replace(/([\w ]+): ([\d\-,.$]+)/g, (_, m2, m3) => {
      if (!(m2.trim() in replaceList)) return ` ${colors[Settings.commandTextColor]+ symbols[Settings.commandSymbol]} ${m2.trim()}&f: ${colors[Settings.commandValueColor]}${m3}`
      return ` ${colors[Settings.commandTextColor]+ symbols[Settings.commandSymbol]} ${replaceList[m2.trim()]}&f: ${colors[Settings.commandValueColor]}${m3}`
    })

  ChatLib.chat(`${colors[Settings.commandTextColor]}> ${msg}`)
}

function handleSlayers(msgString, event) { //!slayers
  cancel(event)

  const msg = msgString
    .replace(/˚/g, "\n")
    .replace(/(Total Slayer EXP):  ?([\d,.]+)/, (_, m1, m2) => ` ${colors[Settings.commandTextColor]+ symbols[Settings.commandSymbol]} ${m1}&f: ${colors[Settings.commandValueColor]}${m2}`)
    .replace(/( [\w]+) level:  ?(\d) ? ?- ?([\d,.]+)(?:xp)?/g, (_, m1, m2, m3) => ` ${colors[Settings.commandTextColor]+ symbols[Settings.commandSymbol]} ${m1} ${colors[Settings.commandValueColor]}${m2}&f: ${colors[Settings.commandValueColor]}${m3}`)

  ChatLib.chat(`${colors[Settings.commandTextColor]}> ${msg}`)
}

function handleSkills(msgString, event) { //!Skills
  cancel(event)

  const msg = msgString
    .replace(/˚/g, "\n")
    .replace(/([\w ]+): ([\d-]+)/g, (_, m2, m3) => ` ${colors[Settings.commandTextColor]+ symbols[Settings.commandSymbol]} ${replaceList[m2.trim()]}&f: ${colors[Settings.commandValueColor]}${m3}`)

  ChatLib.chat(`${colors[Settings.commandTextColor]}> ${msg}`)
}

function handleKuudra(msgString, event) { //!Kuudra
  cancel(event)

  ChatLib.chat(`${colors[Settings.commandTextColor]}> ${msgString
    .replace(/˚/g, "\n")
    .replace(/(Current Faction):/, "\n " + colors[Settings.commandTextColor] + symbols[Settings.commandSymbol] + " $1&f:" + colors[Settings.commandValueColor])
    .replace(/([\w ]+): ([\d-]+)/g, " " + colors[Settings.commandTextColor] + symbols[Settings.commandSymbol] + "$1&f: " + colors[Settings.commandValueColor] + "$2")}`)
}

function handleContest(msgString, event, account) { //!contest
  cancel(event)
  msgString = msgString.replace("Guild > " + account + ": ", colors[Settings.commandTextColor])
  ChatLib.chat(msgString)
}

function handleEvent(msgString, event, account){
  cancel(event);
  if(!Settings.shouldShowEvent) return
  if(Settings.shouldEventPing) World.playSound('random.orb', 1, 0.99);
  msgString = msgString.replace("Guild > " + account + ": ", colors[Settings.commandTextColor]).replace("[EVENT]",colors[Settings.eventTagColor] + "[EVENT]"+colors[Settings.eventTextColor]).replace(":",":"+colors[Settings.eventTimeColor])
  ChatLib.chat(msgString)
}

function handleStatcheck(msgString, event, account){
  cancel(event);
  World.playSound('random.orb', 1, 0.99);
  msgString = msgString.replace("Officer > " + account + ": ", colors[Settings.commandTextColor]).replace("[STATCHECK]",colors[Settings.eventTagColor] + "[STATCHECK]"+colors[Settings.eventTextColor]).replace(". SB",`.\n${colors[Settings.eventTextColor]}SB`).replace(":",":"+colors[Settings.eventTimeColor])
  ChatLib.chat(msgString)
}

function handlePatchnotes(msgString, event, account) { //Is this even still used?  no it isnt
  cancel(event);
  var url = msgString.substring(msgString.indexOf('§b') + 3)
  url = url.substring(0, url.length - 1)
  msgString = new TextComponent(msgString).setClick("open_url", url);
  Client.showTitle('§c§lPATCHNOTES', '', 0, 50, 20);
  World.playSound('random.orb', 1, 0.99);

  msgString = msgString.replaceAll("¨", "\n")
    .replace("NEW PATCHNOTES!", "§a&lNEW PATCHNOTES!&r§");

  ChatLib.chat(msgString);
}
function handleLocation(msgString,event,account) {
  // error handling
}
function handleEquipment(msgString,event,account) {
  // error handling
}
function handleMessage(msgString, event, account) {//Regular messages
  cancel(event);
  console.log("Handling message for account: " + account);
  console.log("Message content: " + msgString);
  let textinput2 = "[" + Settings.discordTagText + "]"
  const pattern = /(\§2Guild >§[A-Za-z0-9_] )([A-Za-z0-9_]{2,16} )(left\.|joined\.)/;
  msgString = msgString.replace("Guild > " + account + ":", "§2Guild >" + colors[Settings.messageAuthorColor])
  if (pattern.test(msgString)) {

    msgString = msgString.replace(pattern, (match, p1, p2, p3) => {
      return `${p1}${p2}§e${p3}`;
    });
  }
  console.log("Message string after initial processing: " + msgString);
  if (msgString.includes(']: ')) {
    textinput2 = msgString.substring(msgString.indexOf('['), msgString.indexOf(']') + 1)

    msgString = msgString.replace(/\[[a-zA-Z]+\]/g, '')

    msgString = msgString.replace(": ", colors[Settings.rankTagColor] + textinput2 + "§f: ")

  } else {
    msgString = msgString.replace(": ", " " + colors[Settings.discordTagColor] + textinput2 + "§f: ")
  }
  let msgString2 = msgString.split(' ')
  console.log("Message string after initial processing: " + msgString);
  for (i = 0; i < msgString2.length; i++) {
    if (msgString2[i].startsWith('l$')) {
      let temp = msgString2[i]
      let link = STuFLib.decode(msgString2[i])
      msgString = msgString.replace(temp, link)
    }
  }
  console.log("Final message string: " + msgString);
  console.log(Settings.discordToggle)
  if (Settings.discordToggle) {
    console.log("Sending message to chat: " + msgString);
    ChatLib.chat(msgString)
  }
}

function handleOfficer(msgString, event, account) {//Officer messages
  cancel(event);
  let textinput2 = "[" + Settings.discordTagText + "]"
  msgString = msgString.replace("Officer > " + account + ":", "§3Staff >" + colors[Settings.messageAuthorColor])

  if (msgString.includes(']: ')) {
    textinput2 = msgString.substring(msgString.indexOf('['), msgString.indexOf(']') + 1)

    msgString = msgString.replace(/\[[a-zA-Z]+\]/g, '')

    msgString = msgString.replace(": ", colors[Settings.rankTagColor] + textinput2 + "§f: ")

  } else {
    msgString = msgString.replace(": ", " " + colors[Settings.discordTagColor] + textinput2 + "§f: ")
  }
  let msgString2 = msgString.split(' ')

  for (i = 0; i < msgString2.length; i++) {
    if (msgString2[i].startsWith('l$')) {
      let temp = msgString2[i]
      let link = STuFLib.decode(msgString2[i])
      msgString = msgString.replace(temp, link)
    }
  }
  if (Settings.discordToggle) ChatLib.chat(msgString)
}

function handleLinksGeneral(event) {//handles general links
  let modified = false
  const message = new Message(event.message).getMessageParts()
  let newMessage = new Message()
  for (let component of message) {
    let filtered = resolveTextComponent(component)
    if (filtered[0]) {
      modified = true
      newMessage.addTextComponent(filtered[1])
    } else {
      newMessage.addTextComponent(component)
    }
  }
  if (modified) {
    cancel(event)
    newMessage.chat()
  }
}

//Settings
register("command", () => Settings.openGUI()).setName("tempest");
register("command", (...args) => {
  let username = args.toString().replaceAll(","," ")
  ignores.push({
    ign: username
  })
  ChatLib.chat(`§2Guild > §aIgnored ${colors[Settings.messageAuthorColor]}` + username)
  FileLib.write("TempestBridge", "ignores.json", JSON.stringify(ignores))

}).setName("bridgeignore")
register("command", (...args) => {
  let username = args.toString().replaceAll(","," ")
  ignores = ignores.filter((a) => { return a.username !== a.username })
  ChatLib.chat(`§2Guild > §cUn-Ignored ${colors[Settings.messageAuthorColor]}` + username)
  FileLib.write("TempestBridge", "ignores.json", JSON.stringify(ignores))
}).setName("bridgeunignore")

//check if sending URL & encodes it
register('messageSent', (msg, event) => {
  let words = msg.split(" ")
  let modified = false

  for (let i = 0; i < words.length; i++) {
    let currentWord = words[i]
    if (currentWord.startsWith("http")) {
      words[i] = STuFLib.encode(currentWord)
      modified = true
    }
  }

  if (modified) {
    let newMessage = words.join(" ")
    ChatLib.say(newMessage)
    cancel(event)
  }
})

register("chat", function (event) {
  let unformattedMessage = event.message.toString().removeFormatting()
  msgString = stripCurl(unformattedMessage.toString());
  let account = accounts.find(acc => msgString.includes(`Guild > ${acc}:`) || msgString.includes(`Officer > ${acc}:`));
  if (account) {
    if (ignorecheck(msgString, ignores, account)) {
      cancel(event)
      return
    }
    if (msgString.includes("'s networth") && msgString.includes("˚")) { //!nw
      handleNetworth(msgString, event, account)
    }
    else if (msgString.includes("'s stats:") && msgString.includes("˚") || msgString.includes("'s stats: On") && msgString.includes("˚")) {
      handleStats(msgString, event, account)
    }
    else if (msgString.includes("'s cata") && msgString.includes("˚")) {
      handleCata(msgString, event, account)
    }
    else if (msgString.includes("'s slayers") && msgString.includes("˚")) {
      handleSlayers(msgString, event, account)
    }
    else if (msgString.includes("'s skills") && msgString.includes("˚")) {
      handleSkills(msgString, event, account)
    }
    else if (msgString.includes("'s kuudra") && msgString.includes("˚")) {
      handleKuudra(msgString, event, account)
    }
    else if (msgString.includes("'s Armor:") && msgString.includes("˚")) {
      //DO STUFF HERE VALVE
    }
    else if (msgString.includes("'s kuudra") && msgString.includes("˚")) {
      handleKuudra(msgString, event, account)
    }
    else if (msgString.includes("[EVENT]")) {
      handleEvent(msgString, event, account)
    }
    else if (msgString.includes("[STATCHECK]")) {
      handleStatcheck(msgString,event,account)
    }
    else if (msgString.includes("The next contest starts in:")) {
      handleContest(msgString, event, account)
    }
    else if (msgString.startsWith("§2Guild > " + account + "§f: NEW PATCHNOTES!")) {
      handlePatchnotes(msgString, event, account)
    }
    else if (msgString.startsWith("Guild > " + account)) {
      handleMessage(msgString, event, account)
    }
    else if (msgString.startsWith("Officer > " + account)) {
      handleOfficer(msgString, event, account)
    }
  } else {
    //handleLinksGeneral(event);
  }
});
