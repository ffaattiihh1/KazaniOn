# Railway.app Migration Kılavuzu

## 1. Railway Hesap Oluşturun
- https://railway.app adresine gidin
- GitHub ile bağlanın

## 2. PostgreSQL Database
```bash
# Railway dashboard'da:
1. New Project → Provision PostgreSQL
2. Environment variables'ı kopyalayın
```

## 3. Backend Deploy
```bash
# KazaniOnBackend klasöründe:
1. GitHub'a push edin
2. Railway'de "Deploy from GitHub"
3. KazaniOnBackend klasörünü seçin
4. Environment variables ekleyin:
   - DATABASE_URL=postgresql://...
   - SPRING_PROFILES_ACTIVE=production
```

## 4. Android App Güncelleme
```kotlin
// ApiService.kt - BASE_URL değiştirin
const val BASE_URL = "https://your-app.railway.app/"
```

## 5. Custom Domain (Opsiyonel)
```bash
# Railway'de:
1. Settings → Domains
2. kazanion-api.com gibi domain ekleyin
```

## Avantajlar
- ❌ Sleep mode yok
- ✅ İlk $5 bedava
- ✅ Hızlı deployment
- ✅ Auto-scaling 