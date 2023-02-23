-module(exercicios1).
-import(lib_misc, [unconsult/2]).
-compile(export_all).

% Testar se um elemento pertence a uma lista
member(X,[X|_]) -> true;
member(X,[_|T]) -> member(X,T);
member(_,[]) -> false.

% Somar todos os elementos de uma lista
sum([]) -> 0;
sum([H|T]) -> H + sum(T).

% Inverter uma lista
reverse(L) -> reverse(L, []).
reverse([H|T], L) -> reverse(T, [H|L]);
reverse([], L) -> L.

% Aplica uma função a todos os elementos de uma lista
map(_,[]) -> [];
map(F,[H|T]) -> [F(H)| map(F,T)].

% Filtra os elementos de uma lista que satisfazem um predicado
filter(_,[]) -> [];
filter(Pred,[H|T]) -> case Pred(H) of
    true -> [H|filter(Pred,T)];
    false -> filter(Pred,T)
end.

% Particiona uma lista em duas listas, uma com os elementos que satisfazem um predicado e outra com os que não satisfazem
partition(_, []) -> {[],[]};
partition(Pred, List) -> {filter(Pred, List), filter(fun(X) -> not(Pred(X)) end, List)}.


update(File, Key, Delta) ->
    {ok, Terms} = file:consult(File),
    Terms1 = do_update(Key, Delta, Terms),
    unconsult(File ++ ".tmp", Terms1).

do_update(Key, Delta, [{Key,Val}|T]) ->
    [{Key,Val+Delta}|T].

