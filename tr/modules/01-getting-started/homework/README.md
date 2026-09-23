# Ödev 01: Hakkımda

Modül 01'in kapsadıklarını kullanarak küçük etkileşimli bir program kurun, sonra
bilerek bozup derleyicinin size ne söylediğini okuyun.

## Görev

Bu deponun dışında herhangi bir yerde, örneğin bir karalama klasöründe,
`AboutMe.java` adında bir dosya oluşturun. Onu **kompakt kaynak dosyası** olarak
yazın, yani `class` bildirimi ve `static` olmadan.

Şunları yapmalı:

1. Kullanıcının adını sorsun.
2. Doğduğu yılı sorsun.
3. İçinde bulunulan yılı sorsun.
4. Adını ve yaklaşık yaşını içeren küçük bir profil yazdırsın; yaş, içinde
   bulunulan yıldan doğum yılının çıkarılmasıdır.

Örnek çalıştırma, yazılan girdiler istemlerin ardından gösterilmiştir:

```
What is your name? Ada
What year were you born? 1990
What year is it now? 2026

--- About Ada ---
Name: Ada
Age this year: 36
```

## Sonra Bozun

Çalıştıktan sonra:

1. Dosyanın ortasında bir yerden noktalı virgül silin.
2. Tekrar çalıştırın.
3. Dosyanın altına, satır numarası ve şapka satırı dahil, tam hata mesajını
   kaydeden bir yorum ekleyin.
4. Noktalı virgülü geri koyun.

Ödevin asıl amacı bu adım. Kariyeriniz boyunca binlerce derleyici hatası
okuyacaksınız ve ilki baskı altındayken olmamalı.

## Kabul Kriterleri

- [ ] Dosya ayrı bir `javac` adımı olmadan `java AboutMe.java` ile çalışıyor.
- [ ] `class` bildirimi ve `static` anahtar kelimesi yok.
- [ ] Soru sormak için `IO.readln`, yazdırmak için `IO.println` kullanıyor.
- [ ] Yaş, kullanıcı tarafından yazılarak değil aritmetikle hesaplanıyor.
- [ ] Yaş birleştirilmiş metin olarak değil sayı olarak yazdırılıyor.
      `"Age: " + year - birth` derlenmez, nedenini çözmek alıştırmanın bir parçası.
- [ ] Dosyanın altındaki bir yorum, ürettiğiniz ve okuduğunuz gerçek bir derleyici
      hatasını kaydediyor.

## İleri Seviye

Kendi dosyanıza `javap` çalıştırın ve derleyicinin uydurduğu sınıf adını bulun:

```bash
javac -d /tmp/hw01 AboutMe.java
javap -cp /tmp/hw01 AboutMe
```

Üretilen sınıf, Modül 01'in söylediğiyle eşleşiyor mu? `main` metodunun imzası
nedir ve statik mi?

## İpucu, yaşta takıldıysanız

`IO.readln` kullanıcı rakam yazsa bile size her zaman bir `String` verir. Java
`"1990"` değerini sessizce sayı gibi kullanmaz.
[`AskingForInput.java`](../../../../modules/01-getting-started/examples/AskingForInput.java)
dosyasının metni `int` değerine nasıl çevirdiğine tekrar bakın.
