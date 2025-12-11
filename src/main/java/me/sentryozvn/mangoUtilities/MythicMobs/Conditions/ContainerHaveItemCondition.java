package me.sentryozvn.mangoUtilities.MythicMobs.Conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

public class ContainerHaveItemCondition implements IEntityCondition {

  MythicLineConfig config;

  public ContainerHaveItemCondition(MythicLineConfig config) {
    this.config = config;
  }

  @Override
  public boolean check(AbstractEntity abstractEntity) {



    return false;
  }
}
