# Google Play Store Yayınlama Kontrol Listesi

## ✅ Backend Hazırlığı
- [ ] Render Paid Plan ($7/ay) - Sleep mode kapatıldı
- [ ] HTTPS SSL sertifikası aktif
- [ ] API endpoints test edildi
- [ ] Database backup alındı
- [ ] Error monitoring kuruldu

## ✅ Android App Hazırlığı
- [ ] Release build oluşturuldu
- [ ] APK signing yapıldı
- [ ] ProGuard/R8 optimizasyonu
- [ ] Crash reports (Firebase Crashlytics)
- [ ] App bundle (.aab) format

## ✅ Play Console
- [ ] Developer account ($25 one-time)
- [ ] App listing oluşturuldu
- [ ] Screenshots (telefon, tablet)
- [ ] App description (Türkçe/İngilizce)
- [ ] Privacy policy URL'i
- [ ] Content rating

## ✅ KVKK Compliance
- [ ] Veri işleme metni
- [ ] Privacy policy güncel
- [ ] Kullanıcı consent mekanizması
- [ ] Veri silme seçeneği

## 🚀 Release Build Komutu
```bash
# Release APK oluşturma:
./gradlew assembleRelease

# App Bundle oluşturma (önerilen):
./gradlew bundleRelease
```

## 📋 Sonraki Adımlar
1. **Şimdi**: Render'ı paid plan'a çevir
2. **Yarın**: Release build test et
3. **Bu hafta**: Play Console setup
4. **Gelecek hafta**: Store'a yükle 