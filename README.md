# VolleyBall UPDATE
🥳 Updating the "VolleyBall" plugin. This simple plugin is designed specifically for private servers. It allows you to create a ball and interact with it in the open world of minecraft.

![VolleyBall gameplay](https://github.com/IEnumerablee/Volleyball/blob/master/volleyball.gif?raw=true)
# Usage
- To create a ball, right-click on a live pufferfish while holding 8 pieces of leather in your hand.
- Throw the ball: Left-click (LMB).
- Throw the ball harder: Left-click (LMB) while running or jumping.
- Throw power slider: Hold SHIFT.
- Pick up the ball: Press SHIFT near the ball.

commands:
- /ballskin: Change the ball's skin.
- /ballsreload: Reload the plugin. Requires the volleyball.reload permission.
# Configs
## config.yml
```yaml
# All speed is measured in BPS - blocks per second

# Config VolleyBall Plugin Update

ENABLE_BALL_CRAFTING: true

IPT: 20 # Number of physics processing iterations per tick. Not recommended to change

GRAVITY: -0.05 # Gravity

DEFAULT_BOUNCE_ENERGY_LOSS: 2 # Default energy loss on bounce. Energy / DEFAULT_BOUNCE_ENERGY_LOSS

MAX_SPEED: 40 # Maximum ball speed
MAX_ROT_SPEED: 100 # Maximum ball rotation speed
ROT_BREAKING_SPEED: 1000 # Maximum rotation braking speed
ROT_SPEED_PROPORTION: 1000 # Rotation acceleration proportion based on speed. Applied when the ball bounces

THROW_CHECKING_SPEED: 0.06 # Throw power slider speed
MAX_THROW_POWER: 19 # Maximum throw power (in BPS)
MIN_THROW_POWER: 8 # Minimum throw power (in BPS)

DESPAWN_TIME: 10000 # Time (in ticks) after which a stationary ball will despawn
DEFAULT_SKULLSKIN: ball # Default ball skin

# Block settings
BLOCKS_ENERGY_LOSSES: # Custom energy losses for specific blocks
  WATER: 5
  SLIME_BLOCK: 1
  MOSS_BLOCK: 3
  HAY_BLOCK: 5
  HONEY_BLOCK: 100
  ICE: 1.2
  PACKED_ICE: 1.1
  BLUE_ICE: 0.9

TRANSPARENT_BLOCKS: # Blocks that the ball can pass through && Most transparent blocks are automatically enabled
  - PINK_PETALS

TEXT:
  SKIN_MENU_TITLE: ">> Skin Selection <<"
  SKIN_ALREADY_SELECTED: "The skin has already been selected"
  CLICK_TO_SELECT: "Click to select this skin"

SOUND:
  SOUND_ID: "BLOCK_BAMBOO_BREAK"
  VOLUME: 6.0
  PITCH: 1.0
```
## skins.yml
```yaml
# skin:
#   name: <name>
#   url: <Insert the ending of the link from the official Minecraft API for the head. You can use the site https://minecraft-heads.com/> example: http://textures.minecraft.net/texture/93c2f1c5d2c8f0e33730c14dca1c1d1e1abd8596b0839d6738d18f46432b6fa6 - insert only the hash at the end
#   perm: <Permission for the ball>      You can create your own               *Optional
#   lock_msg: <Message under the ball for those who don't have permission>     *Optional


# Config VolleyBall Plugin Update

# Use "§" for the default colors.

ball:
  name: §fBall
  url: 93c2f1c5d2c8f0e33730c14dca1c1d1e1abd8596b0839d6738d18f46432b6fa6

premium_ball:
  name: §dPremium Ball
  url: a794284953646833c49db0bae137cc93a774cb6ec8c53d1bcef6cd124a5a71ca
  perm: volleyball.premiumball
  lock_msg: "§cYou can't use this ball!"
```

# Adding a New Ball Skin
The plugin uses the official Minecraft API, so you need to use official Minecraft heads. For example, you can use a site like Minecraft-Heads. In the "url" field, you need to insert the set of characters at the end of the link. For example, for the link:
http://textures.minecraft.net/texture/93c2f1c5d2c8f0e33730c14dca1c1d1e1abd8596b0839d6738d18f46432b6fa6,
in the "url" field, insert:
93c2f1c5d2c8f0e33730c14dca1c1d1e1abd8596b0839d6738d18f46432b6fa6.

You can also set a permission requirement for using a specific ball skin. To do this, use the optional parameters perm and lock_msg. In perm, specify any permission, and in lock_msg, write the message that will be displayed if a player does not have access to this skin.
