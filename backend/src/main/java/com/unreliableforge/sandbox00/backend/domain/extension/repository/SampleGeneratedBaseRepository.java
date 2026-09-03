package com.unreliableforge.sandbox00.backend.domain.extension.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.unreliableforge.sandbox00.backend.domain.generated.entity.Users;
import com.unreliableforge.sandbox00.backend.domain.generated.mapper.UsersMapper;

/*
Service が generated.mapper　を直接使用するのを禁止します。
必ず Repository を経由して DB にアクセスすること。

DynamicSQLを使わないので、この制約もあまり意味をもたなくなったけど、この制約はあるほうがいい。

RepositoryがDBにアクセスする方法は2つあります。
ひとつは自動生成されたmapperを使う方法。
もうひとつは、Mapperを自分で実装して使う方法です。
Mapperを自分で実装する場合は、Dynamic SQLを使うメリットがあまりないように思いますので、プロバイダベースのMapperを使うのがよいでしょう。
自動生成も、SynamiCSQLではなく、mybatis3simpleに変更しました。

Mapper は DBアクセスのためのインターフェース。
Repository はドメインモデルのためのインターフェース。

となっていますが、プロバイダベースの場合はこの区別が曖昧になりがち。というか、もう同じものとしていいのではという気がします。
プロバイダベースのMapperを使う場合は、Repositoryを挟む必要はないので、Repositoryに直接Mapperを実装すること。

ただし、以下のような要件がある場合、Repositoryを挟むことで構造的メリットが得られます。
その場合でも、RepositoryからプロバイダベースのMapper（名前はRepositoryなので、わかりにくい）をAutowiredで注入して使うのがよいでしょう。

- 複数 mapper をまとめて 1 つの操作にしたい
- トランザクション境界を Repository に置きたい
- 複雑なビジネスロジックを Service に置きたくない
- 返却型を変換したい（DTO → Domain）
- キャッシュやロギングを挟みたい    
- 再生成の影響をRepositoryで吸収したい

実際にはトランザクション境界はService層に置くことが多いので、Repositoryを経由するメリットは少ないかもしれない。
ビジネスロジックが複雑でも、それをRepositoryに置くのは、Repositoryの責務を超えてしまう可能性があるため、慎重に行うこと。


プロバイダベースとなったので、ほぼそのまま直接SQLを書くことができます。
DynamicSQLを使った実装よりは格段に楽でしょう。
条件分岐などがある場合でも、DynamicSQLよりはSQL Builderを使った方が楽でしょう。

残っているめんどくさい手順は、SELECT からエンティティを生成する部分だけ。

*/

/**
 * generated/mapperを使うRepositoryのサンプル。
 */
public class SampleGeneratedBaseRepository {

    @Autowired
    private UsersMapper usersMapper;

    public Optional<Users> findBySub(String sub) {

        /*
         * ここでgenerated.mapperを使用して DBアクセスを行う。
         * 複数 mapper をまとめて 1 つの操作にしたいとかあるかもしれないが、そんなことをするくらいなら、JOINでいいとか。
         * 
         */

        return Optional.of(usersMapper.selectByPrimaryKey(sub));

    }
}
