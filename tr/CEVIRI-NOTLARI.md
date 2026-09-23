# Çeviri Notları

Bu klasör, müfredatın Türkçe sürümüdür. İngilizce özgün metinler bir üst dizinde
durur ve ikisi aynı yapıyı paylaşır.

## Neyin çevrildiği, neyin çevrilmediği

| Öğe | Karar |
|---|---|
| Ders metni, başlıklar, tablolar | Çevrildi |
| Kod blokları | **Aynen bırakıldı** |
| Java anahtar kelimeleri ve API adları | İngilizce (`record`, `sealed`, `Optional.orElseGet`) |
| Derleyici çıktısı ve hata mesajları | **Aynen bırakıldı** |
| Dosya ve dizin adları | Aynen bırakıldı |
| Komutlar | Aynen bırakıldı |

**Kod neden çevrilmedi?** Kod blokları `examples/` altındaki gerçek dosyalardan
alıntıdır. Yorumları çevirseydim, alıntı artık çalıştırdığınız dosyayla
eşleşmezdi. Ayrıca projenin en baştaki kuralı kodun ve kod yorumlarının İngilizce
olmasıydı, çünkü okuyacağınız her gerçek Java kod tabanı öyle.

**Hata mesajları neden çevrilmedi?** Derleyici size İngilizce konuşur. Çevrilmiş
bir hata mesajı aratılamaz ve terminalde gördüğünüzle eşleşmez.

## Terim tercihleri

Türkçe yazılım camiasında yerleşmiş İngilizce terimleri olduğu gibi kullandım,
ilk geçtikleri yerde Türkçe karşılığını parantez içinde verdim. Amaç, okuduğunuz
kelimeyi arattığınızda sonuç bulabilmeniz.

| İngilizce | Bu metinde |
|---|---|
| stream | stream (akış) |
| thread | iş parçacığı |
| virtual thread | sanal iş parçacığı |
| record | record |
| sealed | mühürlü (sealed) |
| immutable | değişmez |
| race condition | yarış koşulu |
| exception | istisna |
| checked exception | denetlenen istisna |
| garbage collector | çöp toplayıcı |
| classpath | sınıf yolu (classpath) |
| pattern matching | desen eşleme |
| boxing / autoboxing | kutulama / otomatik kutulama |
| generics | jenerikler |
| type erasure | tip silme |
| pinning | sabitlenme |

`stream`, `record`, `lambda` gibi terimleri çevirmemek bilinçli bir karardır.
Türkçe bir Java geliştiricisi bunları İngilizce kullanır, ve dokümantasyon ile
hata mesajlarında da böyle geçerler.

## Yapı

```
tr/
├── CEVIRI-NOTLARI.md       bu dosya
├── README.md               genel bakış
├── modules/<nn>-<ad>/
│   ├── README.md           dersin çevirisi
│   └── homework/README.md  ödevin çevirisi
└── tasks/                  proje dokümanları
```

Örnek kodlar ve çözümler çevrilmez; her iki dil de aynı `modules/*/examples/` ve
`solutions/` dosyalarını kullanır.

## PDF

```bash
./scripts/build-pdfs.sh --tr     # yalnızca Türkçe
./scripts/build-pdfs.sh          # her iki dil
```

Türkçe PDF'ler `pdf-tr/` klasörüne yazılır.
