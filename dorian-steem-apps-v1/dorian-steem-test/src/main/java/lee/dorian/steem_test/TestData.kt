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
    /**
     * 글의 수정 이력(content_history_api)을 확인할 (저자, permlink) 쌍.
     *
     * [singleRevisionContentLink]는 한 번도 수정되지 않은 글이라 개정판이 1건뿐이다.
     * 본인 계정의 글이므로 이력이 바뀔 일이 없다.
     *
     * [multipleRevisionsContentLink]는 제3자의 글이다. 본인 계정에는 **수정된 글이 하나도
     * 없어서** 다른 선택지가 없었다. 2022년 글이라 이제 수정될 가능성은 낮지만, 저자가
     * 지우거나 다시 수정하면 이 테스트는 깨진다. 그때는 개정판이 2건 이상이고 그중
     * title이 빈 행이 있는 다른 글로 바꾸면 된다.
     *
     * 이 글을 고른 이유는 2·3번째 개정판의 title이 `""`라는 점이다. 수정 op는 바뀐 필드만
     * 실어 보내므로 빈 title은 "제목을 지웠다"가 아니라 "제목은 그대로"라는 뜻이고,
     * ContentHistoryResultDTO가 직전 값을 이어받아 채우는 부분을 이 글로만 검증할 수 있다.
     */
    val singleRevisionContentLink = "dorian-mobileapp" to "170-sds-api"
    val multipleRevisionsContentLink = "chainsmokerslav" to "python-04-39"

    /**
     * 이미지가 없는 글. SDS는 이런 글의 json_metadata에 `"image":{}` — 빈 배열이 아니라
     * **빈 객체** — 를 직접 넣어서 저장한다. 공식 API에는 없는 키다.
     *
     * image를 `List<String>?`으로 선언한 DTO로 파싱하면 Gson이 객체 전체를 포기해서 태그까지
     * 잃는다. 65cfcbd에서 links/users로 겪은 것과 같은 함정이라, 태그가 살아 있는지를
     * 이 글로 검증한다. 작성 앱과 무관하게 이미지가 없으면 재현되므로 본인 계정의 글로
     * 충분하다.
     */
    val contentLinkWithoutImages = singleAccount2 to "475"

    // 저자는 있지만 permlink가 없는 글 / 저자부터 없는 글. SDS는 둘을 구분하지 않고
    // 똑같이 "Link could not be found"로 답한다.
    val invalidPermlinkContentLink = singleAccount to "invalid987655321"
    val invalidContentLink = invalidSingleAccount to "invalid987655321"

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
