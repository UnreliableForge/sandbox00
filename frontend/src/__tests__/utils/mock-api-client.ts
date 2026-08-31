// ApiClientエラーの切り替え。
let shouldError = false;
export const setApiMockError = (v: boolean) => {
  shouldError = v;
};

export const mockApiClient = {
  post: async (url: string, _data: any) => {
    if (shouldError) {
      throw new Error(`Mock POST error for ${url} `);
    }
    // ここはuseMutation(ファイルアップロード)にしか使用しない。
    // 形式はAPIと同じ。すべてのファイルアップロードAPIで戻り値の型は同じにする予定なので、Mockの戻り値も同じでいい。
    return { data: { ok: true } };
  },
} as any;
