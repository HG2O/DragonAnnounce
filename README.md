# 🐉 DragonAnnounce

> Plugin Folia pour annoncer en grande pompe la mort de l'Ender Dragon sur ton serveur Minecraft.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen?style=flat-square&logo=minecraft)
![Folia](https://img.shields.io/badge/Folia-Compatible-purple?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

---

## 📖 Description

**DragonAnnounce** est un plugin léger et performant pour serveurs **Folia 1.21.1** qui déclenche une annonce spectaculaire dès qu'un joueur tue l'Ender Dragon.

- 🎆 Feux d'artifice en étoile autour du dragon
- 📢 Titre + sous-titre + message chat pour tous les joueurs
- 🎵 Sons épiques (mort du dragon + fanfare)
- 💀 Drop automatique de la **tête de dragon**
- ⚙️ Entièrement configurable via `config.yml`

---

## ✨ Fonctionnalités

| Fonctionnalité | Description |
|---|---|
| 📣 Annonce globale | Titre, sous-titre et message chat envoyés à tous les joueurs en ligne |
| 🎆 Feux d'artifice | Burst de 4 feux d'artifice violets/blancs autour de la position du dragon |
| 🔊 Sons | `ENDER_DRAGON_DEATH` + `CHALLENGE_COMPLETE` joués pour chaque joueur |
| 🐉 Tête de dragon | Droppée automatiquement à la mort du dragon |
| ⏱️ Cooldown | Anti-spam de 10 secondes entre deux annonces |
| 🧵 Thread-safe | 100% compatible avec le système de schedulers régionaux de Folia |

---

## 🚀 Installation

1. Télécharge le fichier `.jar` depuis les [Releases](https://github.com/HG2O/DragonAnnounce/releases)
2. Place-le dans le dossier `/plugins` de ton serveur Folia
3. Redémarre le serveur
4. Modifie le `config.yml` généré selon tes préférences
5. Utilise `/reload confirm` ou redémarre pour appliquer les changements

---

## ⚙️ Configuration

Le fichier `config.yml` est généré automatiquement au premier démarrage.

```yaml
# Titre affiché à tous les joueurs (supporte %player%)
title: "🐉 ENDER DRAGON VAINCU 🐉"

# Sous-titre affiché en dessous du titre
subtitle: "%player% a tué le dragon !"

# Message envoyé dans le chat
chat-message: "%player% a tué l'Ender Dragon !"
```

### Variables disponibles

| Variable | Description |
|---|---|
| `%player%` | Remplacé par le nom du joueur qui a tué le dragon |

---

## 🛠️ Compilation (développeurs)

### Prérequis
- Java 21+
- Maven 3.8+

### Build

```bash
git clone https://github.com/HG2O/DragonAnnounce.git
cd DragonAnnounce
mvn clean package
```

Le `.jar` compilé se trouve dans `target/DragonAnnounce-1.0-SNAPSHOT.jar`.

### Dépendances

```xml
<dependency>
    <groupId>dev.folia</groupId>
    <artifactId>folia-api</artifactId>
    <version>1.21.1-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

---

## 🔧 Compatibilité

| Plateforme | Supporté |
|---|---|
| Folia 1.21.1 | ✅ Oui |
| Paper 1.21.1 | ✅ Oui (partiellement) |
| Spigot / Bukkit | ❌ Non recommandé |

> ⚠️ Ce plugin utilise les schedulers régionaux de Folia (`GlobalRegionScheduler`, `RegionScheduler`). Il n'est **pas** compatible avec Spigot/Bukkit natif.

---

## 📜 Licence

Ce projet est sous licence **MIT** — libre d'utilisation, modification et redistribution.

---

## 👤 Auteur

Développé par **HG2O** pour le serveur **ArkaSurvie**.

> *« Le End tremble sous vos pieds »* 🐉
