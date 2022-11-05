package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(
        name = "picture",
        indexes = {
                @Index(columnList = "picture_id", name = "picture_picture_id_uindex", unique = true)
        }
)
public class Picture extends GenericModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "picture_picture_id_seq")
    @SequenceGenerator(sequenceName = "picture_picture_id_seq", name = "picture_picture_id_seq", allocationSize = 1)
    @Column(name = "picture_id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "mime_type", nullable = false)
    private String mimeType;
    @Column(name = "size", nullable = false)
    private Long size;
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSize() {
        return this.size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    protected boolean isSameType(Object o) {
        return o instanceof Picture;
    }
}
