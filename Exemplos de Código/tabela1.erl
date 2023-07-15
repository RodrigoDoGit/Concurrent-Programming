-module(tabela1).
-export([start/0, store/2, lookup/1]).

start() -> register(simpleTable, spawn(fun() -> loop() end)).

store(Key, Value) -> rpc({store, Key, Value}). %%<label id="kvs.store1"/>

lookup(Key) -> rpc({lookup, Key}). %%<label id="kvs.lookup1"/>

rpc(Q) ->
    simpleTable ! {self(), Q},
    receive
	{simpleTable, Reply} ->
	    Reply
    end.

loop() ->  %%<label id="kvs.loop"/>
    receive
	{From, {store, Key, Value}} ->  %%<label id="kvs.store2"/>
	    put(Key, {ok, Value}),
	    From ! {simpleTable, true},
	    loop();
	{From, {lookup, Key}} -> %%<label id="kvs.lookup2"/>
	    From ! {simpleTable, get(Key)},
	    loop()
    end.

%% Abrir uma nova máquina virtual:
%% erl -name gandalf
%% Enviar mensagens entre máquinas diferentes:
%% erl -name bilbo
%% rpc:call('gandalf@MacBook.local',tabela1,store,[ana,porto]).
%%true
%% rpc:call('gandalf@MacBook.local',tabela1,lookup,[ana]).
%% {ok,porto}
