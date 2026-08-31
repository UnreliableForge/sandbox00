import { isRouteErrorResponse, useRouteError } from "react-router-dom";

/**
 * エラーページ。
 * エラーメッセージがそのまま出てしまうのはあまりよろしくない。ということで、いろいろ変更するのもいいかもしれません。
 */
export default function ErrorPage() {
  const error = useRouteError();

  let message = "不明なエラーが発生しました";

  // loader/action のエラー（Response 型）
  if (isRouteErrorResponse(error)) {
    message = `${error.status} ${error.statusText}`;
  }

  // 例外（throw new Error）
  else if (error instanceof Error) {
    message = error.message;
  }

  // それ以外（予期しない型）
  else {
    message = JSON.stringify(error);
  }

  return (
    <div className="p-8 text-center">
      <h1 className="mb-4 text-2xl font-bold">エラーが発生しました</h1>
      <p className="text-gray-600">{message}</p>
    </div>
  );
}
