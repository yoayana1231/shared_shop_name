/*! jQuery v1.10.2 | (c) 2005, 2013 jQuery Foundation, Inc. | jquery.org/license
//@ sourceMappingURL=jquery-1.10.2.min.map
*/
// 画面のHTMLがすべて読み込まれてから実行する
document.addEventListener('DOMContentLoaded', function() {

	document.querySelectorAll('.js-delete-form').forEach(form => {
		form.addEventListener('submit', function(event) {
			// ① まずは通常のフォーム送信（ページ遷移）をストップ
			event.preventDefault();

			// ② SweetAlert2 のポップアップを表示
			Swal.fire({
				title: '本当に削除しますか？',
				text: "この操作は取り消すことができません。",
				icon: 'warning',                  // 警告アイコンを表示
				showCancelButton: true,           // キャンセルボタンを表示
				confirmButtonColor: '#d33',       // 削除ボタンの色（赤系）
				cancelButtonColor: '#3085d6',      // キャンセルボタンの色（青系）
				confirmButtonText: 'YES',
				cancelButtonText: 'NO',
				reverseButtons: true              // ボタンの配置を「キャンセル」「削除」の順にする（お好みで）
			}).then((result) => {
				// ③ ユーザーが「削除する」をクリックした場合のみ、フォームを強制送信
				if (result.isConfirmed) {
					form.submit();
				}
			});
		});
	});

})