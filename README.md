# 📱 SmartVendor — Intelligent Inventory & Sales Management

SmartVendor is an Android application built to help small and medium vendors intelligently track inventory, record sales, digitize receipts, and monitor business performance in real-time. The app leverages Jetpack Compose, Firebase, and Google AI to provide an automated and seamless vendor experience.

## 🚀 Features

### 📄 OCR Receipt Import
- Capture or upload receipt images.
- Automatically extract item name, quantity, unit price, and total.
- Instantly convert receipt data into structured inventory items.

### 📦 Inventory Management
- Add inventory manually or via OCR.
- Real-time inventory updates with Firestore.
- Reorder-level detection & Low Stock Alerts.

### 🛒 Sales Recording
- Record sales per item.
- Automatically update stock, sales count, daily statistics, and analytics.

### 📊 Analytics Dashboard
- Today's Revenue
- Today's Profit
- Total Revenue
- Total Profit
- Items Sold Today
- AI-Generated Business Insights

### 🔒 Vendor Accounts
- Vendor-based data isolation using `vendorId` in Firestore.

## 🛠 Tech Stack

### Frontend
- **Kotlin**
- **Jetpack Compose**
- **MVVM Architecture**
- **Material 3**
- **Coroutines + Flow**

### Backend / Services
- **Firebase Firestore**
- **Firebase Storage**
- **Firebase Authentication**
- **Google AI** (for OCR)

## 🏗 Project Structure

```
com.basebox.smartvendor
│
├── data
│   ├── local.model   # Data classes (InventoryItem, Product, Sale, Receipt…)
│   ├── repository    # Repositories (InventoryRepository, DashboardRepository…)
│   └── remote        # OCR + API integration
│
├── ui
│   ├── screens       # Composable Screens (Dashboard, Inventory, Sales, Camera…)
│   ├── components    # Reusable UI components (Dialogs, cards, modals)
│   └── theme         # App theme and styling
│
└── viewmodel         # ViewModels
    ├── InventoryViewModel
    ├── DashboardViewModel
    └── ReceiptViewModel
```

## 📥 How to Run the App as a Developer

Follow these steps to set up the project on your machine:

### Prerequisites
- Android Studio Iguana or later
- Kotlin 1.9+
- Minimum SDK 24
- A Firebase project
- A Google AI (Gemini) API Key

### 1. Clone the Repository
```bash
git clone https://github.com/your-repo/smartvendor.git
cd smartvendor
```

### 2. Open in Android Studio
1.  Open Android Studio.
2.  Choose **Open an Existing Project**.
3.  Select the project's root folder.

### 3. Configure Firebase
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Create a new project.
3.  Download the `google-services.json` file.
4.  Place it inside the `app/` directory of the project.

### 4. Enable Needed Firebase Services
In the Firebase Console, enable the following services:
- **Firestore**
- **Storage**
- **Authentication** (with Email/Password provider)

### 5. Add the Gemini / OCR API Key
In your `local.properties` file, add your API key:

```properties
GEMINI_API_KEY=YOUR_KEY_HERE
```

### 6. Sync Gradle
Click **Sync Now** in the notification bar that appears in Android Studio.

### 7. Run the App
- Use a Physical Device (recommended) or an Android Emulator (API 30+).
- Press the **Run ▶** button in Android Studio.
