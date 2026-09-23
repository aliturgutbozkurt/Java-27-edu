# Ödev 09: Bir İfade Değerlendirici

Aritmetik ifadeleri mühürlü bir hiyerarşi olarak modelleyin, sonra derleyicinin
bilerek unuttuğunuz bir durumu yakalamasına izin verin.

Bu, mühürlü tipler artı record'lar artı desen eşlemenin klasik kullanımıdır ve
bir kez elle yazmaya değer.

## Birinci Bölüm: Model

`Calculator.java` dosyasını klasik biçimde oluşturun.

Beş record tipine izin veren bir `sealed interface Expr` tanımlayın:

| Record | Bileşenler |
|---|---|
| `Literal` | bir `int value` |
| `Add` | iki `Expr` |
| `Multiply` | iki `Expr` |
| `Negate` | bir `Expr` |
| `Divide` | iki `Expr` |

`Add`, `Multiply`, `Negate` ve `Divide` tiplerinin `int` değil `Expr` tuttuğuna
dikkat edin. Yapıyı bir ağaç ve metotları özyinelemeli yapan şey budur.

## İkinci Bölüm: Üç İşlem

Her biri **`default` içermeyen tek bir switch ifadesi** olan üç metot yazın:

1. `int` sonucu döndüren `evaluate(Expr)`
2. İkili işlemleri parantezleyerek okunaklı bir metin döndüren `print(Expr)`
3. Ağacın kaç düğüm içerdiğini döndüren `countNodes(Expr)`

`(3 + 4) * -(2)` ifadesini kurun ve üçünü de üzerinde çalıştırın.

Bileşenleri çıkarmak için **record desenleri** kullanın.
`case Add a` yazıp sonra `a.left()` çağırmak yerine
`case Add(Expr left, Expr right)` yazın.

## Üçüncü Bölüm: Derleyicinin İzlediğini Kanıtlayın

`permits` cümlesine altıncı bir record olan `Power` ekleyin. `evaluate` metodunu
**güncellemeyin**.

Derleyin. Tam hatayı, isimlendirdiği eksik deseni dahil ederek bir yoruma
kaydedin. Sonra ya `Power` durumunu ele alın ya da onu yeniden kaldırın.

`evaluate` metodunda bir `default` dalı olsaydı bunun yerine ne olacağını bir
cümleyle yazın.

## Dördüncü Bölüm: Sadeleştirme

Şu kurallar için **koşullar** kullanarak, daha basit ama eşdeğer bir `Expr`
döndüren `simplify(Expr)` yazın:

| Kural | Örnek |
|---|---|
| `0 + x` ve `x + 0` | `(0 + 7)` sonucu `7` |
| `1 * x` ve `x * 1` | `(1 * 9)` sonucu `9` |
| `0 * x` ve `x * 0` | `(0 * 9)` sonucu `0` |
| `--x` | `--5` sonucu `5` |
| sabit katlama | `(2 + 3)` sonucu `5` |

Çift olumsuzlama kuralı ilginç olanı. **İç içe** bir record deseni istiyor, tek
bir case etiketinde iki seviye eşleştiriyor.

Burada sıra önemli. Koşullu durumların aynı tip için koşulsuz olandan neden önce
gelmesi gerektiğini bir yorumda söyleyin.

## Kabul Kriterleri

- [ ] `java Calculator.java` ile çalışıyor
- [ ] `Expr` açık bir `permits` cümlesiyle `sealed`
- [ ] `evaluate`, `print` ve `countNodes` her biri **`default` içermeyen** tek switch
- [ ] Parçalamak için erişimci çağrıları değil record desenleri kullanılıyor
- [ ] Bir yorum ürettiğiniz gerçek "missing patterns" hatasını alıntılıyor
- [ ] Bir cümle `default` dalının neye mal olacağını açıklıyor
- [ ] `simplify` beş kuralın hepsini koşullarla uyguluyor
- [ ] Çift olumsuzlama kuralı iç içe bir record deseni kullanıyor
- [ ] Bir yorum koşullu-önce-koşulsuz sıralamasını açıklıyor

## İleri Seviye

Sıfıra bölme, bir `int` döndürerek ele alınamaz. Sonucu, `Success(int value)` ve
`Failure(String reason)` tiplerine izin veren ikinci bir mühürlü arayüz `Result`
olarak modelleyin ve onu döndüren `evaluateSafely` yazın.

Sonra bunu istisna fırlatmakla karşılaştıran bir yorum yazın. Modül 10 sırada
istisnaları anlatıyor, dolayısıyla her yaklaşımın çağırana neye mal olduğunu
okumadan önce not edin.

## İpucu, iç içe desen derlenmiyorsa

`Negate` bir `Expr` tutar, ve `Negate` kendisi de bir `Expr` tir. Dolayısıyla
bileşeni başka bir `Negate` olan bir `Negate`, tipi iki kez isimlendirip ikisini
de parçalayarak eşleştirilir. Dış deseni önce yazın ve içtekini parantezlerinin
içine bırakın.
