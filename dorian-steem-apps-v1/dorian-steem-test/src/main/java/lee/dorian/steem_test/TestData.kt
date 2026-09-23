package lee.dorian.steem_test

object TestData {

    val singleAccount = "dorian-mobileapp"
    val singleAccount2 = "dorian-lee"
    val singleAccount3 = "dorian-dev"
    val invalidSingleAccount = "invalid10293845"
    val invalidSingleAccount2 = "invalid76787654"

    val multipleAccounts = arrayOf(
        singleAccount,
        singleAccount2,
        singleAccount3
    )

    val multipleInvalidAccounts = arrayOf(
        invalidSingleAccount,
        invalidSingleAccount2
    )

    /**
     * 서로 다른 작성 앱(json_metadata.app)을 대표하는 제3자 계정들.
     *
     * 글의 json_metadata 모양은 글을 올린 앱마다 다르다. 예를 들어 steemitkorea/0.1은
     * links/users를 빈 배열이 아니라 빈 객체({})로 내보내는데, 이 때문에 해당 필드를
     * List<String>으로 선언했을 때 Gson이 응답 전체의 파싱에 실패한 적이 있다.
     *
     * 본인 계정만으로 테스트하면 이런 앱별 차이를 놓친다. 각 쌍은 (계정, 작성 앱)이다.
     */
    val accountsByPostingApp = arrayOf(
        "icetea" to "steemit/0.2",
        "labaz" to "(app 없음)",
        "sean2masaaki" to "SteemX",
        "daily-public" to "steempro/0.2",
        "marts" to "steem-mobile/1.0.68",
        "web2.support" to "curator-perfect-steem",
        "maikuraki" to "steemcoinpan/0.1",
        "bokhtiar1444" to "speem/1.1",
        "cr7pt0" to "beem/0.24.26",
        "uco.btc-d" to "upvu",
        "statsexpert" to "stats",
        "tworld" to "total-steemit/1.0",
        "xelenium" to "steem/0.7",
        "jeehun" to "steemitkorea/0.1"
    )

}
