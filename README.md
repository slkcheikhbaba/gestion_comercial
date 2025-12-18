# 🏢 Gestion Commerciale

Application Java de gestion d'entreprise avec base de données PostgreSQL.

## ✨ Fonctionnalités
- **Gestion complète** : Personnes, agences, villes, exploitations
- **Recherche avancée** : Exploration multi-tables avec sélection multiple
- **Export professionnel** : Rapports PDF avec iText 7
- **Interface intuitive** : Calendrier intégré, navigation par onglets

## 🚀 Installation Rapide
``bash
# 1. Cloner le projet
git clone https://github.com/skcheikhbaba/gestion_comercial.git
cd gestion_comercial

# 2. Configurer PostgreSQL
CREATE DATABASE gestion_commerciale;

# 3. Modifier src/main/resources/hibernate.cfg.xml
#    (URL, utilisateur, mot de passe)

# 4. Lancer
mvn clean compile exec:java

📖 Utilisation
Ajouter une personne : Onglet Personnes → Remplir formulaire → 📅 Sélectionner date → Enregistrer
Rechercher : Menu Données → Explorer → Cocher lignes → Exporter PDF
Naviguer : Onglets pour modules différents

🛠️ Technologies
Java 21 + Swing (Interface)
PostgreSQL + Hibernate 6.4.4 (Base de données)
iText 7 (PDF) + JCalendar (Dates)
Maven (Build)

📊 Structure
gestion_comercial/
├── src/main/java/
│   ├── gui/          # Interfaces utilisateur
│   ├── dao/          # Accès aux données  
│   ├── model/        # Entités JPA
│   └── util/         # Utilitaires
└── pom.xml          # Configuration Maven

🔧 Pour les Développeurs
Architecture DAO pattern
Code modulaire, facile à étendre
Documentation dans le code

 Développé par : slkcheikhbab.
