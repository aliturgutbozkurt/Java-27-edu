# Ödev 06: Bir Şeyleri Kaybeden Küme

Birinci bölüm, bilerek sebep olduğunuz bir hata avı. İkinci bölüm, gönderimi
dürüstçe kullanan küçük bir hiyerarşi kuruyor.

## Birinci Bölüm: Bir HashSet Kırın

`Library.java` dosyasını klasik biçimde oluşturun.

Tek bir `String code` tutan `BrokenIsbn` sınıfı yazın. `equals` metodunu, aynı
koda sahip iki `BrokenIsbn` nesnesi eşit olacak şekilde geçersiz kılın.
**`hashCode` metodunu geçersiz kılmayın.**

Sonra `main` içinde:

1. Aynı ISBN değerini bir `HashSet` içine iki kez ekleyin
2. Kümenin boyutunu yazdırın
3. Kümenin eşit bir ISBN `contains` edip etmediğini yazdırın
4. İki nesnenin birbirine `equals` olup olmadığını yazdırın

Çıktı kendisiyle çelişecek. Küme, eşit olduğunu kabul ettiği iki öğe tutacak ve
zaten içerdiği bir öğeyi bulamayacak.

### Teşhis Edin

Bunun neden olduğunu **kendi kelimelerinizle** açıklayan bir yorum yazın. Doğru
bir cevap kovalardan bahsetmek zorunda. "Çünkü hashCode eksik" demek belirtiyi
tarif eder, mekanizmayı değil.

Sonra düzeltilmiş sürüm olan `Isbn` sınıfını yazın ve aynı dört satırın düzgün
davrandığını gösterin.

### Mutasyon Tuzağı

Hem `equals` hem `hashCode` tarafından kullanılan **final olmayan** bir alana
sahip bir sınıf daha yazın. Sonra:

1. Bir örnek oluşturup `HashSet` içine koyun
2. `contains(theObject)` yazdırın
3. Alanı değiştirin
4. `contains(theObject)` ve kümenin boyutunu tekrar yazdırın

Nesne, onu hâlâ sayan bir kümenin içinde kaybolacak. Nedenini bir yorumda
açıklayın ve bunun `equals` içinde kullanılan alanlar hakkında ne ima ettiğini
söyleyin.

## İkinci Bölüm: Karışık Raf

Başlığı olan soyut bir `LibraryItem` ve üç alt sınıf yazın: `Book`, `Audiobook`
ve `Magazine`. Her biri boyutunu farklı ölçer. Kitabın sayfası, sesli kitabın
süresi, derginin sayı numarası vardır.

Gereksinimler:

- `LibraryItem` alt sınıfların uygulamak zorunda olduğu bir `abstract` metot içersin
- `LibraryItem` miras aldıkları sıradan bir metot içersin
- `LibraryItem` neden final olduğunu söyleyen bir yorumla birlikte bir `final`
  metot içersin
- Bir alt sınıf, davranışı değiştirmek yerine genişletmek için
  `super.someMethod()` çağırsın
- Üç alt sınıfın hepsi `final` olsun, bunun neden varsayılan olduğunu söyleyen
  bir yorumla

Sonra bir `LibraryItem` dizisi üzerinde döngü kurup üçünü de yazdırın. Tek döngü,
üç davranış.

## Kabul Kriterleri

- [ ] `java Library.java` ile çalışıyor
- [ ] `BrokenIsbn` çıktısı boyut `2`, `contains` false ve `equals` true gösteriyor
- [ ] Bir yorum kova mekanizmasını kendi kelimelerinizle açıklıyor
- [ ] `Isbn` çıktısı boyut `1` ve `contains` true gösteriyor
- [ ] Mutasyon tuzağı, kümenin boyutu değişmeden `contains` değerinin true'dan
      false'a döndüğünü gösteriyor
- [ ] `LibraryItem` bir soyut, bir sıradan ve bir `final` metot içeriyor
- [ ] Her `@Override` anotasyonlu
- [ ] Döngü `LibraryItem` tipine göre yazılmış, asla bir alt sınıfa göre değil

## İleri Seviye

`Isbn` sınıfını bir record olarak yeniden yazın ve `equals`, `hashCode` ile
`toString` metotlarını silin. Davranışın aynı olduğunu doğrulayın, sonra geçiş
yaparak neyi bıraktığınızı anlatan bir yorum yazın. Bıraktığınız bir şey var ve
Modül 08 onu isimlendirecek.

## İpucu, birinci bölüm yanlış davranmıyorsa

İkinci ekleme ve `contains` çağrısı için **yeni** nesneler kurduğunuzdan emin
olun. Aynı değişkeni tekrar kullanmak bir nesneyi kendisiyle karşılaştırır, bu da
yalnızca kimlikle başarılı olur ve üretmeye çalıştığınız hatayı gizler.
