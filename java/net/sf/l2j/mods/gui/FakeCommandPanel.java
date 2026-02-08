package net.sf.l2j.mods.gui;

import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import net.sf.l2j.gameserver.ThreadPool;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.mods.actor.FakePlayer;
import net.sf.l2j.mods.enums.FakeTeleportPoint;

 
public class FakeCommandPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private final JComboBox<FakeTeleportPoint.Type> typeBox = new JComboBox<>(FakeTeleportPoint.Type.values());
    private final JComboBox<FakeTeleportPoint> pointBox = new JComboBox<>();

    private final JSpinner countSp = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
    private final JSpinner radiusSp = new JSpinner(new SpinnerNumberModel(120, 0, 5000, 10));

    private final JCheckBox useClass = new JCheckBox("Use ClassId (Create)");
    private final JComboBox<ClassId> classBox = new JComboBox<>();

    private final JButton btnCreate = new JButton("CREATE");
    private final JButton btnSpawnPinned = new JButton("SPAWN");
    private final JButton btnDespawnPinned = new JButton("DESPAWN");
    private final JButton btnDeletePinned = new JButton("DELETE DB");

    private final PhantomPanel owner;

    public FakeCommandPanel(PhantomPanel owner)
    {
        this.owner = owner;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(spawnRow());
        add(actionRow());

        reloadPoints();
        typeBox.addActionListener(e -> reloadPoints());

        Arrays.stream(ClassId.VALUES).forEach(cid -> {
            if (FakePlayer.getAllAIs().containsKey(cid))
                classBox.addItem(cid);
        });

        useClass.setSelected(true); // padrão: CREATE
        classBox.setEnabled(true);
        useClass.addActionListener(e -> classBox.setEnabled(useClass.isSelected()));

        wire();
        updateButtons();
    }

    private JPanel spawnRow()
    {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        p.setOpaque(false);
        p.setBorder(new TitledBorder("Spawn"));

        p.add(new JLabel("Type:"));
        p.add(typeBox);

        p.add(new JLabel("Point:"));
        p.add(pointBox);

        p.add(new JLabel("Count:"));
        p.add(countSp);

        p.add(new JLabel("Radius:"));
        p.add(radiusSp);

        p.add(useClass);
        p.add(classBox);

        p.add(btnCreate);
      
        return p;
    }

    private JPanel actionRow()
    {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        p.setOpaque(false);
        p.setBorder(new TitledBorder("Actions (Pinned targets)"));
        p.add(btnSpawnPinned);
        p.add(btnDespawnPinned);
        p.add(btnDeletePinned);
        return p;
    }

    private void reloadPoints()
    {
        pointBox.removeAllItems();
        FakeTeleportPoint.Type type = (FakeTeleportPoint.Type) typeBox.getSelectedItem();
        if (type == null)
            return;

        for (FakeTeleportPoint p : FakeTeleportPoint.values())
            if (p.getType() == type)
                pointBox.addItem(p);
    }

    private void updateButtons()
    {
        boolean hasPinned = owner.getModel().hasPinned();
        btnSpawnPinned.setEnabled(hasPinned);
        btnDespawnPinned.setEnabled(hasPinned);
        btnDeletePinned.setEnabled(hasPinned);
    }

    private void wire()
    {
        btnCreate.addActionListener(e -> {
            final FakeTeleportPoint point = (FakeTeleportPoint) pointBox.getSelectedItem();
            final int count = (Integer) countSp.getValue();
            final int radius = (Integer) radiusSp.getValue();

            final boolean create = useClass.isSelected();
            final ClassId cid = (ClassId) classBox.getSelectedItem();

            ThreadPool.execute(() -> {
                try
                {
                    if (!create)
                        throw new IllegalStateException("Enable 'Use ClassId (Create)' to CREATE new.");

                    List<Integer> created = FakeAdminService.createAtPoint(point, cid, count, radius);

                    // auto-pin os criados
                    owner.runOnEdt(() -> {
                        for (int objId : created)
                            owner.getModel().pin(objId);
                        updateButtons();
                    });

                    owner.toast("CREATE OK: " + created.size() + " @ " + point.getName());
                }
                catch (Exception ex)
                {
                    owner.toast("Create failed: " + ex.getMessage());
                }
            });
        });

        btnSpawnPinned.addActionListener(e -> {
            final FakeTeleportPoint point = (FakeTeleportPoint) pointBox.getSelectedItem();
            final int radius = (Integer) radiusSp.getValue();
            final Set<Integer> pinned = owner.getModel().getPinnedIds();

            ThreadPool.execute(() -> {
                try
                {
                    int spawned = FakeAdminService.spawnPinnedOfflineToPoint(pinned, point, radius);
                    owner.toast("SPAWN PINNED OK: " + spawned + " restored/spawned.");
                }
                catch (Exception ex)
                {
                    owner.toast("Spawn pinned failed: " + ex.getMessage());
                }
            });
        });

        btnDespawnPinned.addActionListener(e -> {
            final Set<Integer> pinned = owner.getModel().getPinnedIds();

            ThreadPool.execute(() -> {
                try
                {
                    int d = FakeAdminService.despawnPinnedOnline(pinned);
                    owner.toast("DESPAWN PINNED OK: " + d);
                }
                catch (Exception ex)
                {
                    owner.toast("Despawn pinned failed: " + ex.getMessage());
                }
            });
        });

        btnDeletePinned.addActionListener(e -> {
            final Set<Integer> pinned = owner.getModel().getPinnedIds();

            ThreadPool.execute(() -> {
                try
                {
                    int d = FakeAdminService.deletePinnedDb(pinned);
                    owner.toast("DELETE DB PINNED OK: " + d);
                }
                catch (Exception ex)
                {
                    owner.toast("Delete pinned failed: " + ex.getMessage());
                }
            });
        });
    }

    // chamado pelo PhantomPanel após refresh/page, para manter botões coerentes
    public void onModelChanged()
    {
        updateButtons();
    }
}
