package com.andrei1058.bedwars.support.version.v1_13_R2;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.server.VersionSupport;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityTargetEvent;

@SuppressWarnings({"unchecked", "SimplifiableConditionalExpression", "ForLoopReplaceableByForEach", "WeakerAccess", "UnclearExpression"})
public class AttackEnemies<T extends EntityLiving> extends PathfinderGoalTarget {

    protected final Class<T> a;
    private final int i;
    protected final PathfinderGoalNearestAttackableTarget.DistanceComparator b;
    protected final Predicate<? super T> c;
    protected T d;
    private ITeam bedWarsTeam;
    private static VersionSupport vs;

    public AttackEnemies(EntityCreature entitycreature, Class<T> oclass, boolean flag, ITeam bedWarsTeam, VersionSupport versionSupport) {
        this(entitycreature, oclass, flag, false);
        this.bedWarsTeam = bedWarsTeam;
        if (vs == null) vs = versionSupport;
    }

    public AttackEnemies(EntityCreature entitycreature, Class<T> oclass, boolean flag, boolean flag1) {
        this(entitycreature, oclass, 10, flag, flag1, null);
    }

    public AttackEnemies(EntityCreature entitycreature, Class<T> oclass, int i, boolean flag, boolean flag1, java.util.function.Predicate<? super T> predicate) {
        super(entitycreature, flag, flag1);
        this.a = oclass;
        this.i = i;
        this.b = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entitycreature);
        this.a(1);
        this.c = (entityliving) -> entityliving == null ? false : isMember(entityliving) ? false : (predicate != null && !predicate.test(entityliving) ? false : (!IEntitySelector.e.test(entityliving) ? false : this.a(entityliving, false)));
    }

    private boolean isMember(EntityLiving t0) {
        return t0 instanceof EntityPlayer && bedWarsTeam.isMember(Bukkit.getPlayer(t0.getName()));
    }

    public boolean a() {
        if (this.i > 0 && this.e.getRandom().nextInt(this.i) != 0) {
            return false;
        } else if (this.a != EntityHuman.class && this.a != EntityPlayer.class) {
            List list = this.e.world.a(this.a, this.a(this.i()), this.c);
            if (list.isEmpty()) {
                return false;
            } else {
                list.sort(this.b);
                for (int x = 0; x < list.size(); x++) {
                    if (vs.isDespawnable(((EntityLiving)list.get(x)).getBukkitEntity())){
                        if (vs.getDespawnablesList().get(((EntityLiving)list.get(x)).getBukkitEntity().getUniqueId()).getTeam() == bedWarsTeam) return false;
                    } else if (!isMember((EntityLiving) list.get(x))) {
                        this.d = (T) list.get(x);
                        return true;
                    }
                }
                return false;
            }
        } else {
            this.d = (T) this.e.world.a(this.e.locX, this.e.locY + (double) this.e.getHeadHeight(), this.e.locZ, this.i(), this.i(), new Function<EntityHuman, Double>() {
                public Double a(EntityHuman entityhuman) {
                    ItemStack itemstack = entityhuman.getEquipment(EnumItemSlot.HEAD);
                    return AttackEnemies.this.e instanceof EntitySkeleton && itemstack.getItem() == Items.SKELETON_SKULL || AttackEnemies.this.e instanceof EntityZombie && itemstack.getItem() == Items.ZOMBIE_HEAD || AttackEnemies.this.e instanceof EntityCreeper && itemstack.getItem() == Items.CREEPER_HEAD ? 0.5D : 1.0D;
                }

                public Double apply(EntityHuman object) {
                    return this.a(object);
                }
            }, (java.util.function.Predicate<EntityHuman>) this.c);
            return this.d != null;
        }
    }

    protected AxisAlignedBB a(double d0) {
        return this.e.getBoundingBox().grow(d0, 4.0D, d0);
    }

    public void c() {
        this.e.setGoalTarget(this.d, this.d instanceof EntityPlayer ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        super.c();
    }
}