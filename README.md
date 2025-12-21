<div align="center">

# 🌟 TDailyRewards v1.04

**Highly customizable daily reward plugin for Minecraft Java Edition.**

![Version](https://img.shields.io/badge/version-1.0.4-blue)
![Java](https://img.shields.io/badge/Java-21-orange)

</div>

---

## 🧐 About

**TDailyRewards** is a very lightweight solution designed with performance in mind. No database setup required (YAML based). It allows server owners to reward players for regular activity with complete control over the rewards.

## 🚀 Features

* ⚡ **Lightweight:** No SQL required, almost zero performance impact.
* ⚙️ **Extremely customizable:** Configure items, custom names, messages, enchantments, scaling, and much more.
* 🎮 **Simple to use:** Players only need one command: `/reward`.
* 🌍 **Multi-language support:** Easily add your own language (English and Polish built-in).

---

## 💻 Commands & Permissions

### 👤 For Players

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/reward` | Gives the player the reward set for the current day. | `tribeDailyRewards.reward` |

### 🛡️ For Admins

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/setRewardLevel <player> <val>` | Sets the specific daily reward day for a player. | `tribeDailyRewards.admin` |
| `/resetRewardTimer <player>` | Resets the timer so the player can claim the reward again immediately. | `tribeDailyRewards.admin` |
| `/rewardInfo <player>` | Displays the player's current level and time until the next reward. | `tribeDailyRewards.admin` |

---
