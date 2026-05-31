<div align="center">
  <img src="https://ik.imagekit.io/d5u8bqewg/PaymentX/finditreadme_logo.png?updatedAt=1753820420321" alt="FindIt Logo" width="60%" />

  # FindIT: Lost & Found App for VIT

  <p align="center">
    <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
    <img src="https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white" />
    <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" />
    <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
    <img src="https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white" />
    <img src="https://img.shields.io/badge/TensorFlow.js-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white" />
  </p>

  <p align="center"><b>Find what's lost, return what's found — fast, secure and student-only.</b></p>
</div>

<p align="center">
  <img src="https://raw.githubusercontent.com/Rudragupta8777/FindIT/master/app/src/main/res/drawable/app_screenshots.png" alt="FindIt App Screenshots" width="80%" />
</p>

## 🌟 Overview

**FindIt** is a campus-exclusive platform built for VIT students to **report**, **search**, and **claim lost items**. With integrated image moderation, QR-based secure claims and a clean mobile interface — it ensures trust, security, and ease of use for the entire student community.

<div align="center">
  <table>
    <tr>
      <td width="50%">
        <h3 align="center">🎒 For Users</h3>
        <ul>
          <li>Post lost/found items with images</li>
          <li>Advanced search with filters</li>
          <li>Claim items via QR verification</li>
          <li>Track all your posts and claims</li>
          <li>Image moderation to prevent abuse</li>
        </ul>
      </td>
      <td width="50%">
        <h3 align="center">🔐 For Admins</h3>
        <ul>
          <li>Manage and moderate content</li>
          <li>Delete unsafe/inappropriate posts</li>
          <li>Track app versioning</li>
          <li>Admin-only secure endpoints</li>
        </ul>
      </td>
    </tr>
  </table>
</div>

## ✨ Key Features

### 🧭 Lost & Found Flow

1. **Report** - Upload images, select category (lost/found), and describe the item
2. **Moderate** - AI-based image moderation prevents NSFW/abusive content
3. **Search** - Use filters or keywords to browse through reported items
4. **Claim** - Found your item? Request claim using the secure QR flow
5. **Verify** - Show your QR to the finder and verify ownership
6. **Complete** - Item handed over, claim closed securely!

> **⚠️ Note:** All interactions are verified with VIT Google login. No outsiders allowed.

### 🛡️ Security Features

<div align="center"> 
  <table> 
    <tr> 
      <td align="center"> 
        <h3>🔐</h3> <b>VIT-Only Access</b><br> 
        <small>Sign in with official college email via Firebase</small> 
      </td> 
      <td align="center"> 
        <h3>🖼️</h3> <b>AI Image Moderation</b><br> 
        <small>NSFW detection via TensorFlow.js + NSFWJS</small> 
      </td> 
      <td align="center"> 
        <h3>🔄</h3> <b>Secure QR Claims</b><br> 
        <small>Time-bound, JWT-authenticated QR codes</small> 
      </td> 
      <td align="center"> 
        <h3>🛠️</h3> <b>Admin Controls</b><br> 
        <small>Manual moderation & unsafe post removal</small> 
      </td> 
    </tr> 
  </table> 
</div>

## 🛠️ Tech Stack

<div align="center">
  <table>
    <tr>
      <td align="center"><img src="https://cdn.simpleicons.org/kotlin" width="50px"/><br/>Kotlin</td>
      <td align="center"><img src="https://cdn.simpleicons.org/nodedotjs" width="50px"/><br/>Node.js</td>
      <td align="center"><img src="https://cdn.simpleicons.org/mongodb" width="50px"/><br/>MongoDB</td>
      <td align="center"><img src="https://cdn.simpleicons.org/firebase" width="50px"/><br/>Firebase</td>
      <td align="center"><img src="https://cdn.simpleicons.org/cloudinary" width="50px"/><br/>Cloudinary</td>
      <td align="center"><img src="https://cdn.simpleicons.org/tensorflow" width="50px"/><br/>TensorFlow.js</td>
    </tr>
  </table>
</div>

---

## 📱 Application Architecture

### System Architecture
```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │      │                 │
│   User Mobile   │◄────►│    FindIT API   │◄────►│   NSFW Check    │
│   Application   │      │    (Backend)    │      │                 │
│                 │      │                 │      └─────────────────┘
└─────────────────┘      └────────┬────────┘      
                                  │
                                  ▼
                         ┌─────────────────┐
                         │                 │
                         │    MongoDB      │
                         │    Database     │
                         │                 │
                         └─────────────────┘
```

### Mobile Architecture
```

FindIT/
├── Authentication
│   └── Google Sign-In (VIT only)
├── Item Feed
│   └── Search Lost Item
│       └── Claim Lost Item
│           └── QR Code Scanner
│               └── Secure Claiming
├── Post Item
│   └── Upload Details + Image (Report Found Item)
│       └── Item Reported Successfully
└── My Profile
    ├── My Activity
    │   ├── My Claims (Claimed Item Details)
    │   └── My Reports (All Reported Items)
    │       └── List of Items Reported & Claimed
    │       └── Generate QR (For Secure Claiming)
    │       └── Delete Item
    ├── Terms & Conditions
    ├── Contact Us
    ├── Developer Team
    └── Sign Out


````

---

## ⚙️ Implementation Status

| Component                | Status          | Notes                                                |
|--------------------------|-----------------|------------------------------------------------------|
| User Authentication      | ✅ Complete    | Google OAuth via Firebase (VIT Email only)            |
| Image Upload & Moderation| ✅ Complete    | Cloudinary + NSFWJS                                   |
| Lost/Found Posting       | ✅ Complete    | Fully functional with filters                         |
| QR Claim Flow            | ✅ Complete    | JWT-secured, time-limited QR claims                   |
| My Items & Claims Page   | ✅ Complete    | Users can manage their history                        |
| Admin Tools              | ✅ Complete    | Moderation & delete functionality                     |
| App Core Features        | ✅ Complete    | Working version deployed, UI refinements ongoing      |

---

## 🚀 Getting Started

### 📲 Download the App

You can **download the FindIT app** directly from our official website:  
🌐 [www.finditapp.me](https://findit-app-new.vercel.app)

Or, download the APK from our [**Initial Release**](https://github.com/Rudragupta8777/FindIT/releases/tag/v1.0.0) GitHub release.

> 🔒 *VIT Google Sign-In is required to access the app.*

---

## 🔗 Related Repositories

- [FindIT Backend](https://github.com/pratham-developer/finditBackend)

## 👥 Meet Our Team

<div align="center">
  <table>
    <tr>
      <td align="center">
        <h3>Rudra Gupta</h3>
        <p><i>App & Website Developer</i></p>
        <a href="https://github.com/Rudragupta8777"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white" height="22" target="_blank"></a>
        <a href="https://www.linkedin.com/in/rudra-gupta-36827828b/"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white" height="22" target="_blank"></a>
      </td>
      <td align="center">
        <h3>Pratham Khanduja</h3>
<p><i>App & Backend Developer</i></p>
<a href="https://github.com/pratham-developer"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white" height="22" target="_blank"></a>
<a href="https://www.linkedin.com/in/pratham-khanduja/"><img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white" height="22" target="_blank"></a>
      </td>
    </tr>
  </table>
</div>
   
  ---
</div>
