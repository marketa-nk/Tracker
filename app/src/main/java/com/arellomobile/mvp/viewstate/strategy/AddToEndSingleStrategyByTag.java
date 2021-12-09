package com.arellomobile.mvp.viewstate.strategy;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.ViewCommand;

import java.util.Iterator;
import java.util.List;

/**
 * Command will be added to end of commands queue. If commands queue contains same type command, then existing command will be removed.
 *
 * Date: 17.12.2015
 * Time: 11:24
 *
 * @author Yuri Shmakov
 */
public class AddToEndSingleStrategyByTag implements StateStrategy {
	@Override
	public <View extends MvpView> void beforeApply(List<ViewCommand<View>> currentState, ViewCommand<View> incomingCommand) {
		Iterator<ViewCommand<View>> iterator = currentState.iterator();

		while (iterator.hasNext()) {
			ViewCommand<View> entry = iterator.next();

			if (entry.getTag().equals(incomingCommand.getTag())) {
				iterator.remove();
				break;
			}
		}

		currentState.add(incomingCommand);
	}

	@Override
	public <View extends MvpView> void afterApply(List<ViewCommand<View>> currentState, ViewCommand<View> incomingCommand) {
		// pass
	}
}
